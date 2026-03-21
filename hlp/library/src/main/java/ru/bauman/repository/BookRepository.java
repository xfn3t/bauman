package ru.bauman.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import ru.bauman.model.Book;
import ru.bauman.common.repository.Repository;

public interface BookRepository extends Repository<Book, Long> {
    Optional<Book> findByTitle(String title, Connection conn) throws SQLException;
    void updateAvailability(Long bookId, boolean available, Connection conn) throws SQLException;
}