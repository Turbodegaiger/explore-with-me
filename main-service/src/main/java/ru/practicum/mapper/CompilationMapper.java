package ru.practicum.mapper;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {
    public static Compilation mapNewCompilationDtoToCompilation(NewCompilationDto compilationDto, List<Event> events) {
        return new Compilation(0L, compilationDto.getTitle(), compilationDto.getPinned(), events);
    }

    public static CompilationDto mapCompilationToCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getEvent(), compilation.getId(), compilation.getPinned(), compilation.getTitle());
    }

    public static List<CompilationDto> mapCompilationToCompilationDtoList(Iterable<Compilation> compilation) {
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation comp : compilation) {
            compilationDtoList.add(mapCompilationToCompilationDto(comp));
        }
        return compilationDtoList;
    }

    public static Compilation mapUpdateToCompilationDto(Long compId, List<Event> events, UpdateCompilationRequest update, Compilation oldCompilation) {
        Compilation updatedCompilation = new Compilation();
        updatedCompilation.setId(compId);
        if (update.getPinned() != null) {
            updatedCompilation.setPinned(update.getPinned());
        } else {
            updatedCompilation.setPinned(oldCompilation.getPinned());
        }
        if (update.getTitle() != null) {
            updatedCompilation.setTitle(update.getTitle());
        } else {
            updatedCompilation.setTitle(oldCompilation.getTitle());
        }
        updatedCompilation.setEvent(events);
        return updatedCompilation;
    }
}
