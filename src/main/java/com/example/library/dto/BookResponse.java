package com.example.library.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private boolean isBorrowed;
    private LocalDateTime createdAt;
}
