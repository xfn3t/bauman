package ru.bauman.dto;

import java.sql.Timestamp;

public record LoanDto(Long id, Long bookItemId, Long readerId, Timestamp borrowDate, Timestamp returnDate) {}