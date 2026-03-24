package ru.bauman.repository;

import java.sql.SQLException;
import java.util.List;
import ru.bauman.dto.ReaderWithBooksDto;
import ru.bauman.model.Reader;
import ru.bauman.common.repository.Repository;

public interface ReaderRepository extends Repository<Reader, Long> {
    List<ReaderWithBooksDto> findAllReadersWithActiveBooks() throws SQLException;
}