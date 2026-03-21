package ru.bauman.mapper;

import java.util.List;
import ru.bauman.dto.LoanDto;
import ru.bauman.model.Loan;

public class LoanMapper {
    public static LoanDto toDto(Loan loan) {
        return new LoanDto(
                loan.getId(),
                loan.getBookId(),
                loan.getReaderId(),
                loan.getBorrowDate(),
                loan.getReturnDate()
        );
    }

    public static Loan toEntity(LoanDto dto) {
        return new Loan(
                dto.id(),
                dto.bookId(),
                dto.readerId(),
                dto.borrowDate(),
                dto.returnDate()
        );
    }

    public static List<LoanDto> toDtoList(List<Loan> loans) {
        return loans.stream().map(LoanMapper::toDto).toList();
    }

    public static List<Loan> toEntityList(List<LoanDto> dtos) {
        return dtos.stream().map(LoanMapper::toEntity).toList();
    }
}