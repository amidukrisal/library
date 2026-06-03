package com.example.library.service;

import com.example.library.dto.BorrowRecordResponse;
import com.example.library.exception.CustomException;
import com.example.library.model.Book;
import com.example.library.model.BorrowRecord;
import com.example.library.model.Borrower;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final BorrowerService borrowerService;
    private final BookService bookService;

    @Transactional
    public BorrowRecordResponse borrowBook(Long borrowerId, Long bookId) {
        log.info("Borrow request: borrowerId={}, bookId={}", borrowerId, bookId);

        Borrower borrower = borrowerService.getBorrowerById(borrowerId);
        Book book = bookService.getBookById(bookId);

        // Ensure the book is available (not currently borrowed by anyone)
        borrowRecordRepository.findActiveRecordsByBookId(bookId).ifPresent(activeRecord -> {
            throw new CustomException(
                    String.format("Book id=%d ('%s') is already borrowed by borrower id=%d and has not been returned.",
                            bookId, book.getTitle(), activeRecord.getBorrower().getId()), HttpStatus.CONFLICT);
        });

        // Mark book as unavailable
        book.setBorrowed(true);
        bookRepository.save(book);

        // Create borrow record
        BorrowRecord record = BorrowRecord.builder()
                .borrower(borrower)
                .book(book)
                .build();
        BorrowRecord saved = borrowRecordRepository.save(record);

        log.info("Borrow record created: id={}", saved.getId());
        return toResponse(saved);

    }

    @Transactional
    public BorrowRecordResponse returnBook(Long borrowerId, Long bookId) {
        log.info("Return request: borrowerId={}, bookId={}", borrowerId, bookId);

        // Validate both entities exist
        borrowerService.getBorrowerById(borrowerId);
        Book book = bookService.getBookById(bookId);

        BorrowRecord record = borrowRecordRepository
                .findByBookIdAndBorrowerId(bookId, borrowerId)
                .orElseThrow(() -> new CustomException(
                        String.format("No active borrow record found for borrowerId=%d and bookId=%d. " +
                                        "Either the book was not borrowed by this borrower, or it has already been returned.",
                                borrowerId, bookId), HttpStatus.NOT_FOUND));

        // Mark return
        record.setReturnedAt(LocalDateTime.now());
        borrowRecordRepository.save(record);

        // Mark book as available again
        book.setBorrowed(false);
        bookRepository.save(book);

        log.info("Book id={} returned by borrower id={}", bookId, borrowerId);
        return toResponse(record);
    }

    private BorrowRecordResponse toResponse(BorrowRecord record) {
        return BorrowRecordResponse.builder()
                .recordId(record.getId())
                .bookId(record.getBook().getId())
                .bookTitle(record.getBook().getTitle())
                .bookIsbn(record.getBook().getIsbn())
                .borrowerId(record.getBorrower().getId())
                .borrowerName(record.getBorrower().getName())
                .borrowedAt(record.getBorrowedAt())
                .returnedAt(record.getReturnedAt())
                .isBorrowed(record.isActive())
                .build();
    }
}
