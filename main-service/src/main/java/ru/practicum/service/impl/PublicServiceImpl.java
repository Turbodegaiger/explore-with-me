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
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventsPublicSearchDto;
import ru.practicum.enums.event.EventSort;
import ru.practicum.enums.event.EventState;
import ru.practicum.exception.IncorrectRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.QEvent;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.PublicService;
import ru.practicum.util.DateTimeUtils;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final CompilationRepository compRepository;

    @Override
    public ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageParams = PageRequest.of(
                fromToPage(from, size), size, Sort.by(Sort.DEFAULT_DIRECTION, "title"));
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
                fromToPage(from, size), size, Sort.by(Sort.DEFAULT_DIRECTION, "name"));
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
        builderTotal.and(event.available.eq(s.getOnlyAvailable()));
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
                fromToPage(s.getFrom(), s.getSize()), s.getSize(), Sort.by(Sort.Direction.DESC, sortBy));
        List<EventShortDto> foundEvents = EventMapper.mapEventToEventShortDtoList(eventRepository.findAll(builderTotal, pageParams));
        log.info("Успешно выгружены события по параметрам: {}. Value: {}", s, foundEvents);
        return ResponseEntity.of(Optional.of(foundEvents));
    }

    @Override
    public ResponseEntity<Object> getEventById(Long id) {
        return null;
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
