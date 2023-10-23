package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.event.EventState;
import ru.practicum.enums.request.RequestStatus;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.IncorrectRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.PrivateService;
import ru.practicum.util.DateTimeUtils;
import ru.practicum.validator.ValidatorForEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrivateServiceImpl implements PrivateService {
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<EventShortDto>> getUserEvents(Long userId, Integer from, Integer size) {
        Optional<User> initiator = userRepository.findById(userId);
        if (initiator.isEmpty()) {
            log.info("Не найден пользователь с id={}.", userId);
            throw new NotFoundException(
                    String.format("User with id=%s is not found, check request.", userId));
        }
        Pageable pageParams = PageRequest.of(
                fromToPage(from, size), size, Sort.by(Sort.Direction.DESC, "createdOn"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageParams);
        List<EventShortDto> shortDtos = EventMapper.mapEventToEventShortDtoList(events);
        log.info("По запросу пользователя id={} успешно выгружены события: {}", userId, shortDtos);
        return ResponseEntity.of(Optional.of(shortDtos));
    }

    @Override
    public ResponseEntity<EventFullDto> createEvent(Long userId, NewEventDto newEventDto) {
        ValidatorForEvent.validateDateOfNewEvent(newEventDto);
        Optional<User> initiator = userRepository.findById(userId);
        if (initiator.isEmpty()) {
            log.info("Не найден пользователь с id={}.", userId);
            throw new NotFoundException(
                    String.format("User with id=%s is not found, check request.", userId));
        }
        Optional<Category> category = categoryRepository.findById(newEventDto.getCategory());
        if (category.isEmpty()) {
            log.info("Событие невозможно создать, не найдена категория id={}. {}", newEventDto.getCategory(), newEventDto);
            throw new NotFoundException(
                    String.format("Field: category. Error: Category with id=%s is not found. Value: " + newEventDto,
                            newEventDto.getCategory()));
        }
        Event newEvent = EventMapper.mapNewEventDtoToEvent(newEventDto, category.get(), initiator.get());
        Event createdEvent = eventRepository.save(newEvent);
        Optional<EventFullDto> createdEventFullDto = Optional.of(
                EventMapper.mapEventToEventFullDto(createdEvent));
        log.info("Успешно создано новое событие: {}", createdEventFullDto);
        return ResponseEntity.of(createdEventFullDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EventFullDto> getUserEvent(Long userId, Long eventId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Не найден пользователь с id={}.", userId);
            throw new NotFoundException(
                    String.format("User with id=%s is not found, check request.", userId));
        }
        Optional<Event> event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event.isEmpty()) {
            log.info("Событие с id={} не найдено или недоступно.", eventId);
            throw new NotFoundException(
                    String.format("Event with id=%s is not found or not available, check request.", eventId));
        }
        EventFullDto eventFullDto = EventMapper.mapEventToEventFullDto(event.get());
        log.info("По запросу пользователя id={} успешно выгружено событие: {}", userId, eventFullDto);
        return ResponseEntity.of(Optional.of(eventFullDto));
    }

    @Override
    @Transactional
    public ResponseEntity<EventFullDto> updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest update) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Не найден пользователь с id={}.", userId);
            throw new NotFoundException(
                    String.format("User with id=%s is not found, check request.", userId));
        }
        Optional<Event> oldEvent = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (oldEvent.isEmpty()) {
            log.info("Событие с id={} не найдено или недоступно.", eventId);
            throw new NotFoundException(
                    String.format("Event with id=%s is not found or not available, check request.", eventId));
        }
        EventState oldState = oldEvent.get().getState();
        EventState newState = ValidatorForEvent.validateStateForUserUpdate(update, oldEvent.get(), oldState);
        LocalDateTime newDateTime = ValidatorForEvent.validateEventDateForUpdate(update, oldEvent.get());
        Category newCategory = oldEvent.get().getCategory();
        if (update.getCategory() != null
                && oldEvent.get().getCategory().getId() != update.getCategory()) {
            Optional<Category> updatedCategory = categoryRepository.findById(update.getCategory());
            if (updatedCategory.isEmpty()) {
                log.info("Категории с id={} не существует.", update.getCategory());
                throw new NotFoundException(
                        String.format("Category with id=%s was not found.", update.getCategory()));
            }
            newCategory = updatedCategory.get();
        }
        Event updatedEvent = EventMapper.mapUpdateToEvent(update, oldEvent.get(), eventId, newState, newCategory, newDateTime);
        Event updateResult = eventRepository.save(updatedEvent);
        EventFullDto updatedEventFullDto = EventMapper.mapEventToEventFullDto(updateResult);
        log.info("Cобытие успешно обновлено пользователем id={}: {}", userId, updateResult);
        return ResponseEntity.of(Optional.of(updatedEventFullDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsToEvent(Long userId, Long eventId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Не найден пользователь с id={}.", userId);
            throw new NotFoundException(
                    String.format("User with id=%s is not found, check request.", userId));
        }
        Optional<Event> event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event.isEmpty()) {
            log.info("Событие с id={} не найдено или недоступно.", eventId);
            throw new NotFoundException(
                    String.format("Event with id=%s is not found or not available, check request.", eventId));
        }
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
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Не найден пользователь с id={}.", userId);
            throw new NotFoundException(
                    String.format("User with id=%s is not found, check request.", userId));
        }
        Optional<Event> event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event.isEmpty()) {
            log.info("Событие с id={} не найдено или недоступно.", eventId);
            throw new NotFoundException(
                    String.format("Event with id=%s is not found or not available, check request.", eventId));
        }
        List<Request> requests = requestRepository.findAllById(update.getRequestIds());
        EventRequestStatusUpdateResult updateResultDto;
        if (RequestStatus.valueOf(update.getStatus()) == RequestStatus.CONFIRMED) {
            List<Request> toConfirm = new ArrayList<>();
            List<Request> toReject = new ArrayList<>();
            int canConfirmCount = event.get().getParticipantLimit() - event.get().getConfirmedRequests().intValue();
            if (canConfirmCount <= 0) {
                changeStatusForRequests(requests, RequestStatus.REJECTED);
                requestRepository.saveAll(requests);
                setEventAsUnavailable(eventId);
                log.info("Исчерпан лимит заявок на участие в событии id={}. Все заявки отклонены.", event.get().getId());
                throw new DataConflictException(String.format(
                        "Request limit for event id=%s was reached. All requests are rejected.", event.get().getId()));
            } else {
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
                    setEventAsUnavailable(eventId);
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
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Не найден пользователь с id={}.", userId);
            throw new NotFoundException(
                    String.format("User with id=%s is not found, check request.", userId));
        }
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        List<ParticipationRequestDto> requestDtoList = RequestMapper.mapRequestToRequestDtoList(requests);
        log.info("Успешно выгружены запросы на участие от пользователя id={}: {}", userId, requestDtoList);
        return ResponseEntity.of(Optional.of(requestDtoList));
    }

    @Override
    public ResponseEntity<ParticipationRequestDto> createRequestToEvent(Long userId, Long eventId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Не найден пользователь с id={}.", userId);
            throw new NotFoundException(
                    String.format("User with id=%s is not found, check request.", userId));
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            log.info("Событие с id={} не найдено или недоступно.", eventId);
            throw new NotFoundException(
                    String.format("Event with id=%s is not found or not available, check request.", eventId));
        }
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
        if (event.get().getState() != EventState.PUBLISHED) {
            log.info("Невозможно создать запрос на участие в неопубликованном событии id={}. " +
                    "Текущий статус: {}", eventId, event.get().getState());
            throw new DataConflictException(
                    String.format("Cannot create request to unpublished event id=%s. " +
                            "Current state: %s", eventId, event.get().getState()));
        }
        if (event.get().getConfirmedRequests() >= event.get().getParticipantLimit() || !event.get().getAvailable()) {
            setEventAsUnavailable(eventId);
            log.info("Невозможно создать запрос на участие в событии id={}, достигнут лимит участников. ", eventId);
            throw new DataConflictException(
                    String.format("Cannot create request to event id=%s, participant limit reached.", eventId));
        }
        RequestStatus status = RequestStatus.PENDING;
        if (!event.get().getRequestModeration() || event.get().getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
        Request newRequest = new Request(0L, event.get(), user.get(), status, DateTimeUtils.getCurrentTime());
        ParticipationRequestDto requestDto = RequestMapper.mapRequestToRequestDto(requestRepository.save(newRequest));
        if (requestDto.getStatus().equals(RequestStatus.CONFIRMED.toString())) {
            updateEventConfirmedRequests(eventId, 1L);
        }
        log.info("Успешно создан запрос на участие в событии id={} пользователем id={}. {}", eventId, userId, requestDto);
        return ResponseEntity.of(Optional.of(requestDto));
    }

    @Override
    public ResponseEntity<ParticipationRequestDto> cancelRequestToEvent(Long userId, Long eventId) {
        Optional<Request> request = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isEmpty()) {
            log.info("Не найден запрос на участие в событии id={} от пользователя с id={}.", eventId, userId);
            throw new NotFoundException(
                    String.format("Request for event id=%s from user id=%s was not found.", eventId, userId));
        }
        request.get().setStatus(RequestStatus.PENDING);
        ParticipationRequestDto canceledRequestDto =
                RequestMapper.mapRequestToRequestDto(requestRepository.save(request.get()));
        log.info("Успешно отменён запрос на участие в событии id={} от пользователя id={}: {}",
                eventId, userId, canceledRequestDto);
        return ResponseEntity.of(Optional.of(canceledRequestDto));
    }

    private void updateEventConfirmedRequests(Long eventId, Long confirmedCount) {
        Optional<Event> event = eventRepository.findById(eventId);
        event.get().setConfirmedRequests(event.get().getConfirmedRequests() + confirmedCount);
        eventRepository.save(event.get());
    }

    private void setEventAsUnavailable(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        event.get().setAvailable(false);
        eventRepository.save(event.get());
    }

    private List<Request> changeStatusForRequests(List<Request> requests, RequestStatus status) {
        for (Request r : requests) {
            if (r.getStatus() != RequestStatus.PENDING) {
                log.info("Для изменения статуса запроса на участие id={} необходимо, чтобы он имел статус PENDING. " +
                        "Текущий статус: {}", r.getId(), r.getStatus());
                throw new DataConflictException(
                        String.format("Cannot change status for request id=%s. Current status must be PENDING. " +
                                "Current status: %s", r.getId(), r.getStatus()));
            }
            r.setStatus(status);
        }
        return requests;
    }

    private int fromToPage(int from, int size) {
        if (from < 0 || size <= 0) {
            log.info("Переданы некорректные параметры from {} или size {}, проверьте правильность запроса.", from, size);
            throw new IncorrectRequestException(String.format(
                    "Incorrect parameters: from OR/AND size. They must be positive numbers. from = %s, size = %s.", from, size));
        }
        float result = (float) from / size;
        return (int) Math.ceil(result);
    }
}
