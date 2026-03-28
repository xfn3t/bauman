package ru.bauman.dto;

import java.sql.Timestamp;

public record ActiveLoanInfoDto(Long loanId, String bookTitle, String bookAuthor,
                                String readerName, Timestamp borrowDate) {
}