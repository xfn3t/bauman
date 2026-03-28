package ru.bauman.model;

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
public class BookItem {
    private Long id;
    private Long bookId;
    private Long statusId;
    private String inventoryNumber;
    private String comment;
}