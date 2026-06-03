package com.example.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "ISBN is Required")
    @Column(nullable = false)
    private String isbn;

    @NotBlank(message = "Title is Required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Author is Required")
    @Column(nullable = false)
    private String author;

    @Column(name = "is_borrowed")
    private boolean isBorrowed = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
