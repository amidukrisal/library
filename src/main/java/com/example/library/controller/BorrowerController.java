package com.example.library.controller;

import com.example.library.dto.BorrowRecordResponse;
import com.example.library.dto.BorrowRequest;
import com.example.library.dto.BorrowerResponse;
import com.example.library.dto.RegisterBorrower;
import com.example.library.model.BorrowRecord;
import com.example.library.service.BorrowRecordService;
import com.example.library.service.BorrowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/borrowers")
@RequiredArgsConstructor
@Tag(name = "Borrowers", description = "Borrower management endpoints")
public class BorrowerController {

    private final BorrowerService borrowerService;
    private final BorrowRecordService borrowRecordService;

    @PostMapping
    @Operation(summary = "Register a Borrower")
    public ResponseEntity<BorrowerResponse> registerBorrower(
            @Valid @RequestBody RegisterBorrower request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(borrowerService.registerBorrower(request));
    }

    @PostMapping("/{borrowerId}/borrow/{bookId}")
    @Operation(summary = "Borrow a book")
    public ResponseEntity<BorrowRecordResponse> borrowBook(
            @PathVariable Long borrowerId,
            @PathVariable Long bookId) {
        return ResponseEntity.ok(borrowRecordService.borrowBook(borrowerId, bookId));
    }

    @PutMapping("/{borrowerId}/return/{bookId}")
    @Operation(summary = "Return a borrowed book by book ID")
    public ResponseEntity<BorrowRecordResponse> returnBook(
            @PathVariable Long borrowerId,
            @PathVariable Long bookId) {
        return ResponseEntity.ok(borrowRecordService.returnBook(borrowerId, bookId));
    }
}
