package ru.bauman.serive.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.bauman.config.DatabaseConfig;
import ru.bauman.dto.ReaderDto;
import ru.bauman.mapper.ReaderMapper;
import ru.bauman.model.Reader;
import ru.bauman.repository.ReaderRepository;
import ru.bauman.serive.ReaderService;

@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;

    @Override
    public ReaderDto registerReader(ReaderDto readerDto) throws SQLException {
        Reader reader = ReaderMapper.toEntity(readerDto);
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                readerRepository.save(reader, conn);
                conn.commit();
                return ReaderMapper.toDto(reader);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<ReaderDto> getAllReaders() throws SQLException {
        List<Reader> readers = readerRepository.findAll();
        return ReaderMapper.toDtoList(readers);
    }
}