package ru.bauman.common.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    T save(T entity, Connection conn) throws SQLException;
    Optional<T> findById(ID id, Connection conn) throws SQLException;
    List<T> findAll() throws SQLException;
    void deleteById(ID id, Connection conn) throws SQLException;
}