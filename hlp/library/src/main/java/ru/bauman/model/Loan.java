package ru.bauman.model;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Loan {
    private Long id;
    private Long bookId;
    private Long readerId;
    private Timestamp borrowDate;
    private Timestamp returnDate;
}