package ru.bauman.mapper;

import ru.bauman.dto.ReaderDto;
import ru.bauman.model.Reader;
import java.util.List;

public class ReaderMapper {

    public static ReaderDto toDto(Reader reader) {
        return new ReaderDto(
                reader.getId(),
                reader.getName(),
                reader.getEmail(),
                reader.getPhone()
        );
    }

    public static Reader toEntity(ReaderDto dto) {
        return new Reader(
                dto.id(),
                dto.name(),
                dto.email(),
                dto.phone()
        );
    }

    public static List<ReaderDto> toDtoList(List<Reader> readers) {
        return readers.stream().map(ReaderMapper::toDto).toList();
    }

    public static List<Reader> toEntityList(List<ReaderDto> dtos) {
        return dtos.stream().map(ReaderMapper::toEntity).toList();
    }
}