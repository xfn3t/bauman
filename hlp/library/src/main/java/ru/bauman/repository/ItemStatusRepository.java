package ru.bauman.repository;

import ru.bauman.common.repository.Repository;
import ru.bauman.model.ItemStatus;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface ItemStatusRepository extends Repository<ItemStatus, Long> {
    Optional<Long> getStatusIdByName(String name, Connection conn) throws SQLException;
}