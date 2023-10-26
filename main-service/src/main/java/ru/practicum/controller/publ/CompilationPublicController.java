package ru.practicum.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicController {
    @Autowired
    private final CompilationService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят public запрос на поиск подборок событий: pinned = {}, from = {}, size = {}", pinned, from, size);
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable Long compId) {
        log.info("Принят public запрос на поиск подборки событий с id = {}", compId);
        return service.getCompilationById(compId);
    }
}
