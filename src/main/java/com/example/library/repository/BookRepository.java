package com.example.library.repository;

import com.example.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByIsbn(String isbn);


    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn AND (b.title <> :title OR b.author <> :author)")
    Optional<Book> findByIsbnTitleAuthor(
            @Param("isbn") String isbn,
            @Param("title") String title,
            @Param("author") String author
    );
}
