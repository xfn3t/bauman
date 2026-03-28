package ru.bauman.serive;

import java.sql.SQLException;
import java.util.List;
import ru.bauman.dto.ReaderDto;

public interface ReaderService {
    ReaderDto registerReader(ReaderDto readerDto) throws SQLException;
    List<ReaderDto> getAllReaders() throws SQLException;
}