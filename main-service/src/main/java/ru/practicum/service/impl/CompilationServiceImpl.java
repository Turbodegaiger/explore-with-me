package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CompilationService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    @Autowired
    private final CompilationRepository compRepository;
    @Autowired
    private final EventRepository eventRepository;

    @Override
    public ResponseEntity<CompilationDto> createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findDistinctByIdIn(newCompilationDto.getEvents());
        Compilation newComp = CompilationMapper.mapNewCompilationDtoToCompilation(newCompilationDto, events);
        CompilationDto compilation = CompilationMapper.mapCompilationToCompilationDto(compRepository.save(newComp));
        log.info("Успешно создана новая подборка: {}", compilation);
        return ResponseEntity.status(HttpStatus.CREATED).body(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Compilation with id=%s was not found.", compId)));
        compRepository.deleteById(compId);
        log.info("Успешно удалена подборка c id={}.", compId);
    }

    @Override
    public ResponseEntity<CompilationDto> updateCompilation(UpdateCompilationRequest update, Long compId) {
        Compilation oldCompilation = compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s was not found.", compId)));
        List<Event> events;
        if (update.getEvents() != null) {
            events = eventRepository.findDistinctByIdIn(update.getEvents());
        } else {
            events = oldCompilation.getEvent();
        }
        Compilation updatedCompilation =
                CompilationMapper.mapUpdateToCompilationDto(compId, events, update, oldCompilation);
        CompilationDto updatedCompilationDto =
                CompilationMapper.mapCompilationToCompilationDto(compRepository.save(updatedCompilation));
        log.info("Успешно обновлена подборка: {}", updatedCompilationDto);
        return ResponseEntity.of(Optional.of(updatedCompilationDto));
    }

    @Override
    public ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageParams = PageRequest.of(
                from > 0 ? from / size : 0, size, Sort.by(Sort.DEFAULT_DIRECTION, "title"));
        Iterable<Compilation> compilations;
        if (pinned == null) {
            compilations = compRepository.findAll(pageParams);
        } else {
            compilations = compRepository.findAllByPinned(pinned, pageParams);
        }
        List<CompilationDto> compilationDtoList = CompilationMapper.mapCompilationToCompilationDtoList(compilations);
        log.info("Успешно выгружены подборки по параметрам " +
                "pinned={}, from={}, size={}. Value: {}", pinned, from, size, compilationDtoList);
        return ResponseEntity.of(Optional.of(compilationDtoList));
    }

    @Override
    public ResponseEntity<CompilationDto> getCompilationById(Long compId) {
        Compilation compilation = compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s was not found.", compId)));
        CompilationDto compilationDto = CompilationMapper.mapCompilationToCompilationDto(compilation);
        log.info("Успешно выгружена подборка по id={}. Value: {}.", compId, compilationDto);
        return ResponseEntity.of(Optional.of(compilationDto));
    }
}
