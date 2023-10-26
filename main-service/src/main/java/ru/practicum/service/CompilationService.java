package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    ResponseEntity<CompilationDto> createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    ResponseEntity<CompilationDto> updateCompilation(UpdateCompilationRequest update, Long compId);

    ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size);

    ResponseEntity<CompilationDto> getCompilationById(Long compId);
}
