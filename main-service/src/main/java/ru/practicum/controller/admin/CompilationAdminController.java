package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.impl.CompilationServiceImpl;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationAdminController {
    @Autowired
    private final CompilationServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Принят admin запрос на создание подборки: {}", newCompilationDto);
        return service.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Принят admin запрос на удаление подборки: {}", compId);
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<CompilationDto> updateCompilation(@RequestBody @Valid UpdateCompilationRequest update,
                                                            @PathVariable Long compId) {
        log.info("Принят admin запрос на обновление подборки: {}, update = {}", compId, update);
        return service.updateCompilation(update, compId);
    }
}
