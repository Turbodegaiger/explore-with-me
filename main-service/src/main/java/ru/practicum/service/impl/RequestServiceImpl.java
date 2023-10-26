package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.event.EventState;
import ru.practicum.enums.request.RequestStatus;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.RequestService;
import ru.practicum.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    @Autowired
    private final RequestRepository requestRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsToEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        List<ParticipationRequestDto> requestDtoList = RequestMapper.mapRequestToRequestDtoList(
                requestRepository.findAllByEventId(eventId));
        log.info("Успешно выгружены запросы на участие для события id={}: {}", eventId, requestDtoList);
        return ResponseEntity.of(Optional.of(requestDtoList));
    }

    @Override
    @Transactional
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(Long userId,
                                                                              Long eventId,
                                                                              EventRequestStatusUpdateRequest update) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        List<Request> requests = requestRepository.findByIdInOrderByCreatedAsc(update.getRequestIds());
        EventRequestStatusUpdateResult updateResultDto;
        if (RequestStatus.valueOf(update.getStatus()) == RequestStatus.CONFIRMED) {
            List<Request> toConfirm = new ArrayList<>();
            List<Request> toReject = new ArrayList<>();
            int canConfirmCount = event.getParticipantLimit() - event.getConfirmedRequests().intValue();
            if (canConfirmCount <= 0) {
                changeStatusForRequests(requests, RequestStatus.REJECTED);
                requestRepository.saveAll(requests);
                updateIsEventAvailable(eventId, false);
                log.info("Исчерпан лимит заявок на участие в событии id={}. Все заявки отклонены.", event.getId());
                throw new DataConflictException(String.format(
                        "Request limit for event id=%s was reached. All requests are rejected.", event.getId()));
            } else {
                updateIsEventAvailable(eventId, true);
                if (requests.size() <= canConfirmCount) {
                    toConfirm = requests;
                } else {
                    for (int i = 0; i < canConfirmCount; i++) {
                        toConfirm.add(requests.get(i));
                    }
                    for (int i = canConfirmCount; i < requests.size(); i++) {
                        toReject.add(requests.get(i));
                    }
                    changeStatusForRequests(toReject, RequestStatus.REJECTED);
                    requestRepository.saveAll(toReject);
                    updateIsEventAvailable(eventId, false);
                }
                changeStatusForRequests(toConfirm, RequestStatus.CONFIRMED);
                updateResultDto = RequestMapper.mapRequestToStatusUpdateDto(requestRepository.saveAll(toConfirm), toReject);
                updateEventConfirmedRequests(eventId, (long) toConfirm.size());
                log.info("Успешно приняты запросы: {}, \r\n Отклонены запросы: {}.", toConfirm, toReject);
            }
        } else {
            changeStatusForRequests(requests, RequestStatus.REJECTED);
            updateResultDto = RequestMapper.mapRequestToStatusUpdateDto(new ArrayList<>(), requestRepository.saveAll(requests));
            log.info("Все запросы были отклонены: {}.", requests);
        }
        log.info("Результат изменения статуса запросов: {}.", updateResultDto);
        return ResponseEntity.of(Optional.of(updateResultDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        List<ParticipationRequestDto> requestDtoList = RequestMapper.mapRequestToRequestDtoList(requests);
        log.info("Успешно выгружены запросы на участие от пользователя id={}: {}", userId, requestDtoList);
        return ResponseEntity.of(Optional.of(requestDtoList));
    }

    @Override
    public ResponseEntity<ParticipationRequestDto> createRequestToEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        Optional<Request> request = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            log.info("Пользователь id={} уже оформил запрос на участие в событии id={}.", userId, eventId);
            throw new AlreadyExistsException(
                    String.format("User id=%s already created request to event id=%s.", userId, eventId));
        }
        Optional<Event> eventBySameUser = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (eventBySameUser.isPresent()) {
            log.info("Невозможно создать запрос от пользователя id={}, " +
                    "который является инициатором выбранного события id={}.", userId, eventId);
            throw new DataConflictException(String.format(
                    "Cannot create request by user id=%s, which is initiator of selected event id=%s.", userId, eventId));
        }
        if (event.getState() != EventState.PUBLISHED) {
            log.info("Невозможно создать запрос на участие в неопубликованном событии id={}. " +
                    "Текущий статус: {}", eventId, event.getState());
            throw new DataConflictException(
                    String.format("Cannot create request to unpublished event id=%s. " +
                            "Current state: %s", eventId, event.getState()));
        }
        if ((event.getConfirmedRequests() >= event.getParticipantLimit())
                && event.getParticipantLimit() != 0) {
            updateIsEventAvailable(eventId, false);
            log.info("Невозможно создать запрос на участие в событии id={}, достигнут лимит ({}) участников. ",
                    eventId, event.getParticipantLimit());
            throw new DataConflictException(
                    String.format("Cannot create request to event id=%s, participant limit (%s) reached.",
                            eventId, event.getParticipantLimit()));
        }
        RequestStatus status = RequestStatus.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
        Request newRequest = new Request(0L, event, user, status, DateTimeUtils.getCurrentTime());
        ParticipationRequestDto requestDto = RequestMapper.mapRequestToRequestDto(requestRepository.save(newRequest));
        if (requestDto.getStatus().equals(RequestStatus.CONFIRMED.toString())) {
            updateEventConfirmedRequests(eventId, 1L);
        }
        log.info("Успешно создан запрос на участие в событии id={} пользователем id={}: {}.", eventId, userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(requestDto);
    }

    @Override
    public ResponseEntity<ParticipationRequestDto> cancelRequestToEvent(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Request id=%s was not found.", requestId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Long eventId = request.getEvent().getId();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        if (request.getStatus() != RequestStatus.CANCELED || request.getStatus() != RequestStatus.REJECTED) {
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
            if (!event.getAvailable()) {
                updateIsEventAvailable(eventId, true);
            }
            request.setStatus(RequestStatus.CANCELED);
        }
        ParticipationRequestDto canceledRequestDto =
                RequestMapper.mapRequestToRequestDto(requestRepository.save(request));
        log.info("Успешно отменён запрос на участие id={} от пользователя id={}: {}.",
                requestId, userId, canceledRequestDto);
        return ResponseEntity.of(Optional.of(canceledRequestDto));
    }

    private void updateEventConfirmedRequests(Long eventId, Long confirmedCount) {
        Event event = eventRepository.findById(eventId).get();
        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedCount);
        eventRepository.save(event);
    }

    private void updateIsEventAvailable(Long eventId, boolean isAvailable) {
        Event event = eventRepository.findById(eventId).get();
        event.setAvailable(isAvailable);
        eventRepository.save(event);
    }

    private void changeStatusForRequests(List<Request> requests, RequestStatus status) {
        for (Request r : requests) {
            if (r.getStatus() != RequestStatus.PENDING) {
                log.info("Для изменения статуса запроса на участие id={} необходимо, чтобы он имел статус PENDING. " +
                        "Текущий статус: {}.", r.getId(), r.getStatus());
                throw new DataConflictException(String.format("Cannot change request id=%s state. " +
                        "There is PENDING status needed. Current status is: %s.", r.getId(), r.getStatus()));
            }
            r.setStatus(status);
        }
    }
}
