package ru.practicum.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.MainServiceClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventsPublicSearchDto;
import ru.practicum.enums.event.EventSort;
import ru.practicum.enums.event.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.QEvent;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.PublicService;
import ru.practicum.util.DateTimeUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {
    @Autowired
    MainServiceClient client;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final CompilationRepository compRepository;

    @Override
    public ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageParams = PageRequest.of(
                from > 0 ? from / size : 0, size, Sort.by(Sort.DEFAULT_DIRECTION, "title"));
        List<CompilationDto> compilationDtoList = CompilationMapper.mapCompilationToCompilationDtoList(
                compRepository.findAllByPinned(pinned, pageParams));
        log.info("Успешно выгружены подборки по параметрам " +
                "pinned={}, from={}, size={}. Value: {}", pinned, from, size, compilationDtoList);
        return ResponseEntity.of(Optional.of(compilationDtoList));
    }

    @Override
    public ResponseEntity<CompilationDto> getCompilationById(Long compId) {
        Optional<Compilation> compilation = compRepository.findById(compId);
        if (compilation.isEmpty()) {
            log.info("Не найдена подборка с id={}.", compId);
            throw new NotFoundException(
                    String.format("Compilation with id=%s is not found, check request.", compId));
        }
        CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation.get());
        log.info("Успешно выгружена подборка по id={}. Value: {}.", compId, compilationDto);
        return ResponseEntity.of(Optional.of(compilationDto));
    }

    @Override
    public ResponseEntity<List<CategoryDto>> getCategories(Integer from, Integer size) {
        Pageable pageParams = PageRequest.of(
                from > 0 ? from / size : 0, size, Sort.by(Sort.DEFAULT_DIRECTION, "name"));
        List<CategoryDto> categoryDtoList = CategoryMapper.mapCategoryToCategoryDtoList(
                categoryRepository.findAll(pageParams));
        log.info("Успешно выгружены категории по параметрам " +
                "from={}, size={}. Value: {}", from, size, categoryDtoList);
        return ResponseEntity.of(Optional.of(categoryDtoList));
    }

    @Override
    public ResponseEntity<CategoryDto> getCategoryById(Long catId) {
        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isEmpty()) {
            log.info("Не найдена категория с id={}.", catId);
            throw new NotFoundException(
                    String.format("Category with id=%s is not found, check request.", catId));
        }
        CategoryDto categoryDto = CategoryMapper.mapCategoryToCategoryDto(category.get());
        log.info("Успешно выгружена категория по id={}. Value: {}.", catId, categoryDto);
        return ResponseEntity.of(Optional.of(categoryDto));
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEvents(EventsPublicSearchDto s) {
        QEvent event = QEvent.event;
        BooleanBuilder builderTotal = new BooleanBuilder();
        builderTotal.and(event.state.eq(EventState.PUBLISHED));
        if (s.getOnlyAvailable()) {
            builderTotal.and(event.available.eq(false));
        } else {
            builderTotal.and(event.available.eq(true));
        }
        if (!s.getText().isEmpty()) {
            String text = s.getText().toLowerCase();
            BooleanBuilder builder = new BooleanBuilder();
            builder.or(event.annotation.contains(text));
            builder.or(event.description.contains(text));
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
            builderTotal.and(event.eventDate.before(DateTimeUtils.formatToLocalDT(s.getRangeEnd())));
        }
        String sortBy = "createdOn";
        if (s.getSort() != null) {
            if (s.getSort() == EventSort.EVENT_DATE) {
                sortBy = "eventDate";
            }
            if (s.getSort() == EventSort.VIEWS) {
                sortBy = "views";
            }
        }
        Pageable pageParams = PageRequest.of(
                s.getFrom() > 0 ? s.getFrom() / s.getSize() : 0, s.getSize(), Sort.by(Sort.Direction.DESC, sortBy));
        List<EventShortDto> foundEvents = EventMapper.mapEventToEventShortDtoList(eventRepository.findAll(builderTotal, pageParams));
        log.info("Успешно выгружены события по параметрам: {}. Values: {}", s, foundEvents);
        return ResponseEntity.of(Optional.of(foundEvents));
    }

    @Override
    public ResponseEntity<EventFullDto> getEventById(Long eventId, HttpServletRequest request) {
        saveEndpointHit(request);
        Optional<Event> event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED);
        if (event.isEmpty()) {
            log.info("Событие с id={} не найдено или недоступно.", eventId);
            throw new NotFoundException(
                    String.format("Event with id=%s is not found or not available, check request.", eventId));
        }
        updateViews(eventId);
        EventFullDto fullDto = EventMapper.mapEventToEventFullDto(event.get());
        log.info("Успешно выгружено событие id={}. Value: {}", eventId, fullDto);
        return ResponseEntity.of(Optional.of(fullDto));
    }

    private void saveEndpointHit(HttpServletRequest request) {
        EndpointHit hit = new EndpointHit(
                0,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                DateTimeUtils.formatToString(DateTimeUtils.getCurrentTime()));
        client.saveHit(hit);
        log.info("Сохранение обращение по эндпоинту: {}.", hit);
    }

    private void updateViews(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        event.get().setViews(event.get().getViews() + 1);
        eventRepository.save(event.get());
    }
}
