package com.example.library.service;

import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.exception.CustomException;
import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @Test
    void registerBook_Success() {
        BookRequest request = new BookRequest();
        request.setIsbn("123456789");
        request.setTitle("Shirlock Holmes");
        request.setAuthor("Arthur Conan Doile");

        when(bookRepository.findByIsbn(anyString())).thenReturn(List.of());
        when(bookRepository.save(any(Book.class))).thenReturn(new Book());

        BookResponse result = bookService.registerBook(request);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void registerBook_ISBNInconsistency_ThrowsException() {
        BookRequest request = new BookRequest();
        request.setIsbn("123456789");
        request.setTitle("Title Changed");
        request.setAuthor("Author Changed");

        Book existingBook = Book.builder()
                .isbn("123456789")
                .title("Shirlock Holmes")
                .author("Arthur Conan Doile")
                .build();
        when(bookRepository.findByIsbn("123456789")).thenReturn(List.of(existingBook));

        assertThrows(CustomException.class, () -> bookService.registerBook(request));
    }

    @Test
    void registerBook_multiCopiesSameIsbn_allowed() {
        BookRequest request = new BookRequest();
        request.setIsbn("123456789");
        request.setTitle("Shirlock Holmes");
        request.setAuthor("Arthur Conan Doile");

        Book existingBook = Book.builder()
                .id(1L)
                .isbn("123456789")
                .title("Shirlock Holmes")
                .author("Arthur Conan Doile")
                .isBorrowed(false)
                .build();

        Book savedBook = Book.builder()
                .id(2L)
                .isbn("123456789")
                .title("Shirlock Holmes")
                .author("Arthur Conan Doile")
                .isBorrowed(true)
                .build();

        when(bookRepository.findByIsbn("123456789"))
                .thenReturn(List.of())
                .thenReturn(List.of(existingBook));

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // Both calls succeed
        bookService.registerBook(request);
        bookService.registerBook(request);

        verify(bookRepository, times(2)).save(any(Book.class));
    }
}
