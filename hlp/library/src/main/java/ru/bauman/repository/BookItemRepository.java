package ru.bauman.repository;

import ru.bauman.common.repository.Repository;
import ru.bauman.model.BookItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookItemRepository extends Repository<BookItem, Long> {
    List<BookItem> findByBookId(Long bookId, Connection conn) throws SQLException;
    Optional<BookItem> findAvailableByBookId(Long bookId, Connection conn) throws SQLException;
    void updateStatus(Long bookItemId, Long statusId, Connection conn) throws SQLException;
    int countByBookId(Long bookId, Connection conn) throws SQLException;
    int countAvailableByBookId(Long bookId, Connection conn) throws SQLException;
}