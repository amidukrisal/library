package com.example.library.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BorrowRecordResponse {

    private Long recordId;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long borrowerId;
    private String borrowerName;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
    private boolean isBorrowed;
}
