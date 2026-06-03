package com.example.library.service;

import com.example.library.dto.BorrowRecordResponse;
import com.example.library.model.Book;
import com.example.library.model.BorrowRecord;
import com.example.library.model.Borrower;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowRecordServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerService borrowerService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BorrowRecordService borrowRecordService;

    @Test
    void borrowBook_success() {

        Borrower borrower = Borrower.builder()
                .id(1L).name("Amidu").email("amidu@example.com")
                .createdAt(LocalDateTime.now()).build();

        Book book = Book.builder()
                .id(10L).isbn("123456789").title("Shirlock Holmes")
                .author("Arthur Conan Doile").isBorrowed(false)
                .createdAt(LocalDateTime.now()).build();

        when(borrowerService.getBorrowerById(1L)).thenReturn(borrower);
        when(bookService.getBookById(10L)).thenReturn(book);
        when(borrowRecordRepository.findActiveRecordsByBookId(10L)).thenReturn(Optional.empty());

        BorrowRecord record = BorrowRecord.builder()
                .id(100L).borrower(borrower).book(book)
                .borrowedAt(LocalDateTime.now()).build();
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(record);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BorrowRecordResponse response = borrowRecordService.borrowBook(1L, 10L);

        assertThat(response.getRecordId()).isEqualTo(100L);
        assertThat(response.getBorrowerId()).isEqualTo(1L);
        assertThat(response.getBookId()).isEqualTo(10L);
        assertThat(response.isBorrowed()).isTrue();
        verify(bookRepository).save(argThat(Book::isBorrowed));
    }

    @Test
    void returnBook_success() {

        Borrower borrower = Borrower.builder()
                .id(1L).name("Amidu").email("amidu@example.com")
                .createdAt(LocalDateTime.now()).build();

        Book book = Book.builder()
                .id(10L).isbn("123456789").title("Shirlock Holmes")
                .author("Arthur Conan Doile").isBorrowed(false)
                .createdAt(LocalDateTime.now()).build();

        when(borrowerService.getBorrowerById(1L)).thenReturn(borrower);
        when(bookService.getBookById(10L)).thenReturn(book);

        BorrowRecord activeRecord = BorrowRecord.builder()
                .id(100L).borrower(borrower).book(book)
                .borrowedAt(LocalDateTime.now()).build();
        when(borrowRecordRepository.findByBookIdAndBorrowerId(10L, 1L))
                .thenReturn(Optional.of(activeRecord));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(activeRecord);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BorrowRecordResponse response = borrowRecordService.returnBook(1L, 10L);

        assertThat(response.getRecordId()).isEqualTo(100L);
        verify(bookRepository).save(argThat(b -> !b.isBorrowed()));
    }
}
