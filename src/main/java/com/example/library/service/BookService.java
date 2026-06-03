package com.example.library.service;

import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.exception.CustomException;
import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponse registerBook(BookRequest request) {
        log.info("Registering new book with ISBN: {}", request.getIsbn());

        List<Book> existingBooks = bookRepository.findByIsbn(request.getIsbn());
        if (!existingBooks.isEmpty()) {
            Book existing = existingBooks.get(0);
            if (!existing.getTitle().equals(request.getTitle()) ||
                    !existing.getAuthor().equals(request.getAuthor())) {
                throw new CustomException(
                        "ISBN " + request.getIsbn() + " is already associated with different title/author", HttpStatus.CONFLICT
                );
            }
        }
        Book book = Book.builder()
                .isbn(request.getIsbn().trim())
                .title(request.getTitle().trim())
                .author(request.getAuthor().trim())
                .isBorrowed(false)
                .build();

        Book saved = bookRepository.save(book);
        log.info("Registered book with id={}, ISBN={}", saved.getId(), saved.getIsbn());
        return toResponse(saved);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new CustomException("Book not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    public BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isBorrowed(book.isBorrowed())
                .createdAt(book.getCreatedAt())
                .build();
    }

}
