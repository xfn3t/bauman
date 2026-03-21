package ru.bauman.dto;

import java.sql.Timestamp;

public record LoanDto(Long id, Long bookId, Long readerId, Timestamp borrowDate, Timestamp returnDate) {}