package ru.bauman;

import ru.bauman.repository.BookRepository;
import ru.bauman.repository.LoanRepository;
import ru.bauman.repository.ReaderRepository;
import ru.bauman.repository.impl.BookRepositoryImpl;
import ru.bauman.repository.impl.LoanRepositoryImpl;
import ru.bauman.repository.impl.ReaderRepositoryImpl;
import ru.bauman.serive.BookService;
import ru.bauman.serive.LoanService;
import ru.bauman.serive.ReaderService;
import ru.bauman.serive.impl.BookServiceImpl;
import ru.bauman.serive.impl.LoanServiceImpl;
import ru.bauman.serive.impl.ReaderServiceImpl;
import ru.bauman.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        BookRepository bookRepository = new BookRepositoryImpl();
        ReaderRepository readerRepository = new ReaderRepositoryImpl();
        LoanRepository loanRepository = new LoanRepositoryImpl();

        BookService bookService = new BookServiceImpl(bookRepository);
        ReaderService readerService = new ReaderServiceImpl(readerRepository);
        LoanService loanService = new LoanServiceImpl(bookRepository, readerRepository, loanRepository);

        ConsoleUI ui = new ConsoleUI(bookService, readerService, loanService);
        ui.start();
    }
}