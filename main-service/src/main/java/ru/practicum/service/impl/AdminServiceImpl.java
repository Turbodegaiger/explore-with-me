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
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.enums.event.EventState;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.IncorrectRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.AdminService;
import ru.practicum.util.DateTimeUtils;
import ru.practicum.validator.ValidatorForEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final CompilationRepository compRepository;

    @Override
    @Transactional
    public ResponseEntity<CategoryDto> createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(CategoryMapper.mapNewCategoryDtoToCategory(newCategoryDto));
        CategoryDto categoryDto = CategoryMapper.mapCategoryToCategoryDto(category);
        log.info("Успешно создана новая категория: {}", categoryDto);
        return ResponseEntity.of(Optional.of(categoryDto));
    }

    @Override
    public void removeCategory(Long catId) {
        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isEmpty()) {
            log.info("Категории с id={} не существует.", catId);
            throw new NotFoundException(
                    String.format("Category with id=%s was not found.", catId));
        }
        Optional<Event> event = eventRepository.findByCategoryIdEquals(catId);
        if (event.isEmpty()) {
            categoryRepository.deleteById(catId);
            log.info("Успешно удалена категория: {}", catId);
        } else {
            log.info("Не удалось удалить категорию с id={}: на данную категорию ссылаются события.", catId);
            throw new DataConflictException("The category is not empty.");
        }
    }

    @Override
    public ResponseEntity<CategoryDto> updateCategory(CategoryDto update, Long catId) {
        Category category = categoryRepository.save(CategoryMapper.mapCategoryDtoToCategory(update, catId));
        CategoryDto updatedCategoryDto = CategoryMapper.mapCategoryToCategoryDto(category);
        log.info("Успешно обновлена категория: {}", updatedCategoryDto);
        return ResponseEntity.of(Optional.of(updatedCategoryDto));
    }

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
                fromToPage(from, size), size, Sort.by(Sort.Direction.DESC, "createdOn"));
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
        Iterable<Event> foundEvents = eventRepository.findAll(builderTotal, pageParams);
        log.info("Успешно выведен список событий по переданным параметрам: {}.", foundEvents);
        return ResponseEntity.of(
                Optional.of(EventMapper.mapEventToEventFullDtoList(foundEvents)));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EventFullDto> updateEvent(UpdateEventAdminRequest update, Long eventId) {
        Optional<Event> oldEvent = eventRepository.findById(eventId);
        if (oldEvent.isEmpty()) {
            log.info("События с id={} не существует.", eventId);
            throw new NotFoundException(
                    String.format("Event with id=%s was not found.", eventId));
        }
        EventState newState = ValidatorForEvent.validateStateForAdminUpdate(update, oldEvent.get());
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
        log.info("Cобытие успешно обновлено администратором: {}", updateResult);
        return ResponseEntity.of(Optional.of(updatedEventFullDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserDto>> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageParams = PageRequest.of(fromToPage(from, size), size, Sort.by(Sort.Direction.ASC, "id"));
        List<UserDto> userDtoList;
        if (ids.isEmpty()) {
            userDtoList = UserMapper.mapUsersToUserDtoList(userRepository.findAll(pageParams));
        } else {
            userDtoList = UserMapper.mapUsersToUserDtoList(userRepository.findAllByIdIn(ids, pageParams));
        }
        log.info("Успешно выгружен список пользователей по id: {}. Результат: {}", ids, userDtoList);
        return ResponseEntity.of(Optional.of(userDtoList));
    }

    @Override
    public ResponseEntity<UserDto> createUser(NewUserRequest newUserRequest) {
        User newUser = userRepository.save(UserMapper.mapNewUserRequestToUser(newUserRequest));
        UserDto userDto = UserMapper.mapUserToUserDto(newUser);
        log.info("Успешно создан новый пользователь: {}", userDto);
        return ResponseEntity.of(Optional.of(userDto));
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Пользователя с id={} не существует.", userId);
            throw new NotFoundException(
                    String.format("User with id=%s was not found.", userId));
        }
        userRepository.deleteById(userId);
        log.info("Успешно удалён пользователь c id={}.", userId);
    }

    @Override
    public ResponseEntity<CompilationDto> createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findDistinctByIdIn(newCompilationDto.getEvents());
        Compilation newComp = CompilationMapper.mapNewCompilationDtoToCompilation(newCompilationDto, events);
        CompilationDto compilation = CompilationMapper.mapCompilationToCompilationDto(compRepository.save(newComp));
        log.info("Успешно создана новая подборка: {}", compilation);
        return ResponseEntity.of(Optional.of(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        Optional<Compilation> compilation = compRepository.findById(compId);
        if (compilation.isEmpty()) {
            log.info("Подборки с id={} не существует.", compId);
            throw new NotFoundException(
                    String.format("Compilation with id=%s was not found.", compId));
        }
        compRepository.deleteById(compId);
        log.info("Успешно удалена подборка c id={}.", compId);
    }

    @Override
    public ResponseEntity<CompilationDto> updateCompilation(UpdateCompilationRequest update, Long compId) {
        Optional<Compilation> oldCompilation = compRepository.findById(compId);
        if (oldCompilation.isEmpty()) {
            log.info("Подборки с id={} не существует.", compId);
            throw new NotFoundException(
                    String.format("Compilation with id=%s was not found.", compId));
        }
        List<Event> events;
        if (update.getEvents() != null) {
            events = eventRepository.findDistinctByIdIn(update.getEvents());
        } else {
            events = oldCompilation.get().getEvent();
        }
        Compilation updatedCompilation = CompilationMapper.mapUpdateToCompilationDto(compId, events, update, oldCompilation.get());
        CompilationDto updatedCompilationDto = CompilationMapper.mapCompilationToCompilationDto(compRepository.save(updatedCompilation));
        log.info("Успешно обновлена подборка: {}", updatedCompilationDto);
        return ResponseEntity.of(Optional.of(updatedCompilationDto));
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
