package ru.bauman.ui;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import ru.bauman.common.exception.BookNotAvailableException;
import ru.bauman.common.exception.BookNotFoundException;
import ru.bauman.common.exception.ReaderNotFoundException;
import ru.bauman.dto.ActiveLoanInfoDto;
import ru.bauman.dto.BookDto;
import ru.bauman.dto.BookWithBorrowDateDto;
import ru.bauman.dto.LoanDto;
import ru.bauman.dto.ReaderDto;
import ru.bauman.serive.BookService;
import ru.bauman.serive.LoanService;
import ru.bauman.serive.ReaderService;

public class ConsoleUI {
    private final BookService bookService;
    private final ReaderService readerService;
    private final LoanService loanService;
    private final Scanner scanner;

    public ConsoleUI(BookService bookService, ReaderService readerService, LoanService loanService) {
        this.bookService = bookService;
        this.readerService = readerService;
        this.loanService = loanService;
        this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    }

    public void start() {
        while (true) {
            printMenu();
            int choice = readInt("Выберите пункт: ");
            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> listAllBooks();
                    case 3 -> findBookByTitle();
                    case 4 -> registerReader();
                    case 5 -> listAllReaders();
                    case 6 -> issueBook();
                    case 7 -> returnBook();
                    case 8 -> viewReaderBooks();
                    case 9 -> showPopularBooks();
                    case 10 -> showAllBorrowedBooks();
                    case 0 -> {
                        System.out.println("До свидания!");
                        return;
                    }
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (SQLException e) {
                System.err.println("Ошибка базы данных: " + e.getMessage());
            } catch (BookNotFoundException | ReaderNotFoundException | BookNotAvailableException e) {
                System.err.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\nСистема управления библиотекой");
        System.out.println("1. Добавить книгу");
        System.out.println("2. Показать все книги");
        System.out.println("3. Найти книгу по названию");
        System.out.println("4. Зарегистрировать читателя");
        System.out.println("5. Показать всех читателей");
        System.out.println("6. Выдать книгу читателю");
        System.out.println("7. Вернуть книгу");
        System.out.println("8. Посмотреть книги, выданные читателю");
        System.out.println("9. Показать популярные книги (топ 10)");
        System.out.println("10. Показать все выданные книги");
        System.out.println("0. Выход");
    }

    private void addBook() throws SQLException {
        System.out.print("Введите название: ");
        String title = scanner.nextLine();
        System.out.print("Введите автора: ");
        String author = scanner.nextLine();
        System.out.print("Введите ISBN: ");
        String isbn = scanner.nextLine();
        int copies = readInt("Введите количество экземпляров: ");

        BookDto dto = new BookDto(null, title, author, isbn, copies, copies);
        BookDto saved = bookService.addBook(dto);
        System.out.printf("Книга добавлена успешно с ID: %d (всего экземпляров: %d, доступно: %d)%n",
                saved.id(), saved.totalCopies(), saved.availableCopies());
    }

    private void listAllBooks() throws SQLException {
        List<BookDto> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("Книг нет.");
            return;
        }

        List<String> headers = List.of("ID", "Название", "Автор", "ISBN", "Всего", "Доступно");
        List<String[]> rows = new ArrayList<>();
        for (BookDto b : books) {
            rows.add(new String[]{
                    String.valueOf(b.id()),
                    b.title(),
                    b.author(),
                    b.isbn(),
                    String.valueOf(b.totalCopies()),
                    String.valueOf(b.availableCopies())
            });
        }
        printTable(headers, rows);
    }

    private void findBookByTitle() throws SQLException {
        System.out.print("Введите название (или часть): ");
        String title = scanner.nextLine();
        Optional<BookDto> book = bookService.findBookByTitle(title);
        if (book.isPresent()) {
            BookDto b = book.get();
            System.out.printf("ID: %d | %s | %s | ISBN: %s | Всего: %d | Доступно: %d%n",
                    b.id(), b.title(), b.author(), b.isbn(), b.totalCopies(), b.availableCopies());
        } else {
            System.out.println("Книга с таким названием не найдена.");
        }
    }

    private void registerReader() throws SQLException {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите телефон: ");
        String phone = scanner.nextLine();

        ReaderDto dto = new ReaderDto(null, name, email, phone);
        ReaderDto saved = readerService.registerReader(dto);
        System.out.println("Читатель зарегистрирован с ID: " + saved.id());
    }

    private void listAllReaders() throws SQLException {
        List<ReaderDto> readers = readerService.getAllReaders();
        if (readers.isEmpty()) {
            System.out.println("Читателей нет.");
            return;
        }

        List<String> headers = List.of("ID", "Имя", "Email", "Телефон");
        List<String[]> rows = new ArrayList<>();
        for (ReaderDto r : readers) {
            rows.add(new String[]{
                    String.valueOf(r.id()),
                    r.name(),
                    r.email(),
                    r.phone()
            });
        }
        printTable(headers, rows);
    }

    private void issueBook() throws SQLException, BookNotFoundException, ReaderNotFoundException, BookNotAvailableException {
        Long bookId = readLong("Введите ID книги: ");
        Long readerId = readLong("Введите ID читателя: ");
        loanService.issueBook(bookId, readerId);
        System.out.println("Книга выдана успешно.");
    }

    private void returnBook() throws SQLException, BookNotFoundException, BookNotAvailableException {
        Long bookId = readLong("Введите ID книги: ");
        loanService.returnBook(bookId);
        System.out.println("Книга возвращена успешно.");
    }

    private void viewReaderBooks() throws SQLException, ReaderNotFoundException {
        Long readerId = readLong("Введите ID читателя: ");
        List<BookWithBorrowDateDto> books = loanService.getBooksIssuedToReaderWithDates(readerId);
        if (books.isEmpty()) {
            System.out.println("У этого читателя нет выданных книг.");
            return;
        }

        List<String> headers = List.of("ID", "Название", "Автор", "ISBN", "Дата выдачи");
        List<String[]> rows = new ArrayList<>();
        for (BookWithBorrowDateDto b : books) {
            rows.add(new String[]{
                    String.valueOf(b.bookId()),
                    b.title(),
                    b.author(),
                    b.isbn(),
                    b.borrowDate() != null ? b.borrowDate().toString() : ""
            });
        }
        printTable(headers, rows);
    }

    private void showPopularBooks() throws SQLException {
        List<BookDto> books = loanService.getPopularBooks();
        if (books.isEmpty()) {
            System.out.println("Нет данных о выдачах.");
            return;
        }

        List<String> headers = List.of("ID", "Название", "Автор", "ISBN");
        List<String[]> rows = new ArrayList<>();
        for (BookDto b : books) {
            rows.add(new String[]{
                    String.valueOf(b.id()),
                    b.title(),
                    b.author(),
                    b.isbn()
            });
        }
        printTable(headers, rows);
    }

    private void showAllBorrowedBooks() throws SQLException {
        List<ActiveLoanInfoDto> loans = loanService.getAllActiveLoansWithDetails();
        if (loans.isEmpty()) {
            System.out.println("Нет выданных книг.");
            return;
        }

        List<String> headers = List.of("ID выдачи", "Книга", "Автор", "Читатель", "Дата выдачи");
        List<String[]> rows = new ArrayList<>();
        for (ActiveLoanInfoDto loan : loans) {
            rows.add(new String[]{
                    String.valueOf(loan.loanId()),
                    loan.bookTitle(),
                    loan.bookAuthor(),
                    loan.readerName(),
                    loan.borrowDate() != null ? loan.borrowDate().toString() : ""
            });
        }
        printTable(headers, rows);
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Введите число.");
            scanner.next();
            System.out.print(prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private Long readLong(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextLong()) {
            System.out.println("Введите число.");
            scanner.next();
            System.out.print(prompt);
        }
        long value = scanner.nextLong();
        scanner.nextLine();
        return value;
    }


    private void printTable(List<String> headers, List<String[]> rows) {
        if (rows.isEmpty()) {
            System.out.println("Нет данных.");
            return;
        }

        // Вычисляем максимальную ширину для каждой колонки
        int[] widths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            widths[i] = headers.get(i).length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length && i < widths.length; i++) {
                if (row[i] != null) {
                    widths[i] = Math.max(widths[i], row[i].length());
                }
            }
        }

        // Форматируем разделитель
        StringBuilder sep = new StringBuilder();
        for (int w : widths) {
            sep.append("+").append("-".repeat(w + 2));
        }
        sep.append("+");
        System.out.println(sep);

        // Печатаем заголовки
        System.out.print("|");
        for (int i = 0; i < headers.size(); i++) {
            System.out.printf(" %-" + widths[i] + "s |", headers.get(i));
        }
        System.out.println();
        System.out.println(sep);

        // Печатаем строки
        for (String[] row : rows) {
            System.out.print("|");
            for (int i = 0; i < row.length; i++) {
                String cell = row[i] == null ? "" : row[i];
                System.out.printf(" %-" + widths[i] + "s |", cell);
            }
            System.out.println();
        }
        System.out.println(sep);
    }
}