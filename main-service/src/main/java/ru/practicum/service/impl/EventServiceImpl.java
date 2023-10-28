package ru.practicum.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.MainServiceClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.event.*;
import ru.practicum.dto.like.EventLikeStatisticsDto;
import ru.practicum.dto.like.LikeDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.enums.event.EventSort;
import ru.practicum.enums.event.EventState;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.IncorrectRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LikeMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.practicum.service.EventService;
import ru.practicum.util.DateTimeUtils;
import ru.practicum.util.UpdateHelper;
import ru.practicum.validator.ValidatorForEvent;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    @Autowired
    private final MainServiceClient client;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final LocationRepository locRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final LikeRepository likeRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<EventFullDto>> getEvents(List<Long> users,
                                                        List<String> states,
                                                        List<Long> categories,
                                                        String rangeStart,
                                                        String rangeEnd,
                                                        Integer from,
                                                        Integer size) {
        Pageable pageParams = PageRequest.of(
                from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        QEvent event = QEvent.event;
        BooleanBuilder builderTotal = new BooleanBuilder();
        if (!users.isEmpty()) {
            BooleanBuilder builder = new BooleanBuilder();
            for (Long id : users) {
                builder.or(event.initiator.id.eq(id));
            }
            builderTotal.and(builder);
        }
        if (!states.isEmpty()) {
            BooleanBuilder builder = new BooleanBuilder();
            for (String state : states) {
                builder.or(event.state.eq(EventState.valueOf(state)));
            }
            builderTotal.and(builder);
        }
        if (!categories.isEmpty()) {
            BooleanBuilder builder = new BooleanBuilder();
            for (Long id : categories) {
                builder.or(event.category.id.eq(id));
            }
            builderTotal.and(builder);
        }
        if (!rangeStart.isEmpty()) {
            builderTotal.and(event.eventDate.after(DateTimeUtils.formatToLocalDT(rangeStart)));
        }
        if (!rangeEnd.isEmpty()) {
            builderTotal.and(event.eventDate.before(DateTimeUtils.formatToLocalDT(rangeEnd)));
        }
        List<EventFullDto> foundEvents = EventMapper.mapEventToEventFullDtoList(
                eventRepository.findAll(builderTotal, pageParams));
        log.info("Успешно выведен список событий по переданным параметрам: {}.", foundEvents);
        return ResponseEntity.of(Optional.of(foundEvents));
    }

    @Override
    public ResponseEntity<EventFullDto> updateEvent(UpdateEventRequest update, Long eventId) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        EventState newState = UpdateHelper.getStateForAdminUpdate(update, oldEvent);
        LocalDateTime newDateTime = UpdateHelper.getEventDateForUpdate(update, oldEvent);
        Category newCategory = oldEvent.getCategory();
        if (update.getCategory() != null
                && oldEvent.getCategory().getId().equals(update.getCategory())) {
            Optional<Category> updatedCategory = categoryRepository.findById(update.getCategory());
            if (updatedCategory.isEmpty()) {
                log.info("Категории с id={} не существует.", update.getCategory());
                throw new NotFoundException(
                        String.format("Category with id=%s was not found.", update.getCategory()));
            }
            newCategory = updatedCategory.get();
        }
        Event updatedEvent = EventMapper.mapUpdateToEvent(update, oldEvent, eventId, newState, newCategory, newDateTime);
        EventFullDto updatedEventFullDto = EventMapper.mapEventToEventFullDto(eventRepository.save(updatedEvent));
        log.info("Cобытие успешно обновлено администратором: {}", updatedEventFullDto);
        return ResponseEntity.of(Optional.of(updatedEventFullDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<EventShortDto>> getUserEvents(Long userId, Integer from, Integer size) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Pageable pageParams = PageRequest.of(
                from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageParams);
        List<EventShortDto> shortDtos = EventMapper.mapEventToEventShortDtoList(events);
        log.info("По запросу пользователя id={} успешно выгружены события: {}", userId, shortDtos);
        return ResponseEntity.of(Optional.of(shortDtos));
    }

    @Override
    public ResponseEntity<EventFullDto> createEvent(Long userId, NewEventDto newEventDto) {
        ValidatorForEvent.validateDateOfNewEvent(newEventDto);
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id=%s was not found.", newEventDto.getCategory())));
        LocationEntity newLocation = locRepository.save(
                new LocationEntity(0L, newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon()));
        Event newEvent = EventMapper.mapNewEventDtoToEvent(newEventDto, category, initiator, newLocation);
        Event createdEvent = eventRepository.save(newEvent);
        EventFullDto createdEventFullDto = EventMapper.mapEventToEventFullDto(createdEvent);
        log.info("Успешно создано новое событие: {}", createdEventFullDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEventFullDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EventFullDto> getUserEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        EventFullDto eventFullDto = EventMapper.mapEventToEventFullDto(event);
        log.info("По запросу пользователя id={} успешно выгружено событие: {}", userId, eventFullDto);
        return ResponseEntity.of(Optional.of(eventFullDto));
    }

    @Override
    @Transactional
    public ResponseEntity<EventFullDto> updateUserEvent(Long userId, Long eventId, UpdateEventRequest update) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Event oldEvent = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        EventState oldState = oldEvent.getState();
        if (oldState.equals(EventState.PUBLISHED)) {
            log.info("Невозможно изменить событие id={}, оно должно иметь статус PENDING или CANCELED. " +
                    "Текущий статус: {}", eventId, oldState);
            throw new DataConflictException(
                    String.format("Cannot update event id=%s, it must have state PENDING or CANCELED. " +
                            "Current state is: %s.", eventId, oldState));
        }
        EventState newState = UpdateHelper.getStateForUserUpdate(update, oldEvent, oldState);
        LocalDateTime newDateTime = UpdateHelper.getEventDateForUpdate(update, oldEvent);
        Category newCategory = oldEvent.getCategory();
        if (update.getCategory() != null
                && oldEvent.getCategory().getId().equals(update.getCategory())) {
            Optional<Category> updatedCategory = categoryRepository.findById(update.getCategory());
            if (updatedCategory.isEmpty()) {
                log.info("Категории с id={} не существует.", update.getCategory());
                throw new NotFoundException(
                        String.format("Category with id=%s was not found.", update.getCategory()));
            }
            newCategory = updatedCategory.get();
        }
        Event updatedEvent = EventMapper.mapUpdateToEvent(update, oldEvent, eventId, newState, newCategory, newDateTime);
        Event updateResult = eventRepository.save(updatedEvent);
        EventFullDto updatedEventFullDto = EventMapper.mapEventToEventFullDto(updateResult);
        log.info("Cобытие успешно обновлено пользователем id={}: {}", userId, updateResult);
        return ResponseEntity.of(Optional.of(updatedEventFullDto));
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEvents(EventsPublicSearchDto s, HttpServletRequest request) {
        saveEndpointHit(request);
        QEvent event = QEvent.event;
        BooleanBuilder builderTotal = new BooleanBuilder();
        builderTotal.and(event.state.eq(EventState.PUBLISHED));
        if (s.getOnlyAvailable()) {
            builderTotal.and(event.available.eq(true));
        }
        if (!s.getText().isEmpty()) {
            String text = s.getText().toLowerCase();
            BooleanBuilder builder = new BooleanBuilder();
            builder.or(event.annotation.containsIgnoreCase(text));
            builder.or(event.description.containsIgnoreCase(text));
            builderTotal.and(builder);
        }
        if (!s.getCategories().isEmpty()) {
            BooleanBuilder builder = new BooleanBuilder();
            for (Long id : s.getCategories()) {
                builder.or(event.category.id.eq(id));
            }
            builderTotal.and(builder);
        }
        if (s.getPaid() != null) {
            builderTotal.and(event.paid.eq(s.getPaid()));
        }
        if (!s.getRangeStart().isEmpty()) {
            builderTotal.and(event.eventDate.after(DateTimeUtils.formatToLocalDT(s.getRangeStart())));
        } else {
            builderTotal.and(event.eventDate.after(DateTimeUtils.getCurrentTime()));
        }
        if (!s.getRangeEnd().isEmpty()) {
            LocalDateTime start = DateTimeUtils.formatToLocalDT(s.getRangeStart());
            LocalDateTime end = DateTimeUtils.formatToLocalDT(s.getRangeEnd());
            if (end.isAfter(start)) {
                builderTotal.and(event.eventDate.before(DateTimeUtils.formatToLocalDT(s.getRangeEnd())));
            } else {
                log.info("Получен некорректный запрос: rangeEnd {} должен быть после rangeStart {}.",
                        end, start);
                throw new IncorrectRequestException(
                        String.format("rangeEnd %s must be after rangeStart %s.", end, start));
            }
        }
        String sortBy = "createdOn";
        if (s.getSort() != null) {
            if (s.getSort() == EventSort.EVENT_DATE) {
                sortBy = "eventDate";
            }
            if (s.getSort() == EventSort.VIEWS) {
                sortBy = "views";
            }
            if (s.getSort() == EventSort.RATING) {
                sortBy = "rating";
            }
        }
        Pageable pageParams = PageRequest.of(
                s.getFrom() > 0 ? s.getFrom() / s.getSize() : 0, s.getSize(), Sort.by(Sort.Direction.DESC, sortBy));
        List<Event> events = eventRepository.findAll(builderTotal, pageParams).toList();
        List<EventShortDto> foundEvents = EventMapper.mapEventToEventShortDtoList(events);
        log.info("Успешно выгружены события по параметрам: {}. Values: {}", s, foundEvents);
        return ResponseEntity.of(Optional.of(foundEvents));
    }

    @Override
    public ResponseEntity<EventFullDto> getEventById(Long eventId, HttpServletRequest request) {
        saveEndpointHit(request);
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        updateViews(event, request.getRequestURI());
        EventFullDto fullDto = EventMapper.mapEventToEventFullDto(event);
        log.info("Успешно выгружено событие id={}. Value: {}", eventId, fullDto);
        return ResponseEntity.of(Optional.of(fullDto));
    }

    @Override
    public ResponseEntity<LikeDto> setLikeOrDislikeToEvent(Long userId, Long eventId, Boolean isLike) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        Like newLike;
        long changeRating;
        Optional<Like> like = likeRepository.findByEventIdAndUserId(eventId, userId);
        if (like.isPresent()) {
            if (isLike == like.get().getIsLiked()) {
                log.info("Пользователь id={} уже ставил оценку событию id={}.", userId, eventId);
                throw new AlreadyExistsException(String.format("User id=%s is already rated event id=%s.", userId, eventId));
            } else {
                like.get().setIsLiked(isLike);
                newLike = likeRepository.save(like.get());
                changeRating = isLike ? 2L : -2L;
            }
        } else {
            newLike = likeRepository.save(new Like(0L, event, user, isLike));
            changeRating = isLike ? 1L : -1L;
        }
        updateLikeCount(event, changeRating);
        log.info("Пользователь id={} оставил оценку событию id={}: isLike={}.", userId, eventId, isLike);
        return ResponseEntity.status(HttpStatus.CREATED).body(LikeMapper.mapLikeToLikeDto(newLike));
    }

    @Override
    public void deleteLikeOrDislikeToEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        Optional<Like> like = likeRepository.findByEventIdAndUserId(eventId, userId);
        if (like.isPresent()) {
            deleteLike(like.get());
            log.info("Пользователь id={} снял оценку с события id={}.", userId, eventId);
        }
    }

    @Override
    public ResponseEntity<EventLikeStatisticsDto> getEventLikeStatistics(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s is not found or not available, check request.", eventId)));
        List<UserShortDto> usersLiked = likeRepository.findByEventIdAndIsLiked(eventId, true).stream()
                .map((like) -> UserMapper.mapUserToUserShortDto(like.getUser()))
                .collect(Collectors.toList());
        List<UserShortDto> usersDisliked = likeRepository.findByEventIdAndIsLiked(eventId, false).stream()
                .map((like) -> UserMapper.mapUserToUserShortDto(like.getUser()))
                .collect(Collectors.toList());
        EventLikeStatisticsDto likeStatisticsDto = new EventLikeStatisticsDto(
                EventMapper.mapEventToEventShortDto(event),
                event.getRating(),
                usersLiked,
                usersDisliked);
        log.info("Пользователь id={} выгрузил статистику оценок для события id={}: {}.", userId, eventId, likeStatisticsDto);
        return ResponseEntity.of(Optional.of(likeStatisticsDto));
    }

    private LikeDto deleteLike(Like like) {
        likeRepository.delete(like);
        Long changeRating = like.getIsLiked() ? -1L : 1L;
        updateLikeCount(like.getEvent(), changeRating);
        return LikeMapper.mapLikeToLikeDto(like);
    }

    private void updateLikeCount(Event event, Long change) {
        event.setRating(event.getRating() + change);
        eventRepository.save(event);
    }

    private void saveEndpointHit(HttpServletRequest request) {
        EndpointHit hit = new EndpointHit(
                0,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                DateTimeUtils.formatToString(DateTimeUtils.getCurrentTime()));
        client.saveHit(hit);
        log.info("Сохранение обращения по эндпоинту: {}.", hit);
    }

    private void updateViews(Event event, String uri) {
        ResponseEntity<Object> stats = client.getStats(
                DateTimeUtils.formatToString(event.getCreatedOn()),
                DateTimeUtils.formatToString(DateTimeUtils.getCurrentTime()),
                List.of(uri),
                true);
        if (stats == null || stats.getBody() == null || !stats.getStatusCode().is2xxSuccessful()) {
            log.info("Произошла ошибка при попытке получить данные из stats-server: {}", stats);
            return;
        }
        ViewStats viewStats = ViewStatsMapper.mapStringToViewStats(stats.getBody().toString());
        event.setViews(viewStats.getHits().longValue());
        eventRepository.save(event);
    }
}
