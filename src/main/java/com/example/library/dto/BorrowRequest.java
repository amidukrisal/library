package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BorrowRequest {

    @NotBlank(message = "Borrower ID is Required")
    private String borrowerId;

    @NotBlank(message = "Book ID is Required")
    private String bookId;
}
