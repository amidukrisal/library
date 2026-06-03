package com.example.library.service;

import com.example.library.dto.BorrowerResponse;
import com.example.library.dto.RegisterBorrower;
import com.example.library.exception.CustomException;
import com.example.library.model.Borrower;
import com.example.library.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    @Transactional
    public BorrowerResponse registerBorrower(RegisterBorrower request) {
        log.info("Registering new borrower with email: {}", request.getEmail());

        if (borrowerRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(
                    "A borrower with email '" + request.getEmail() + "' is already registered", HttpStatus.CONFLICT);
        }
        Borrower borrower = Borrower.builder()
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .build();

        Borrower saved = borrowerRepository.save(borrower);
        log.info("Registered borrower with id={}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Borrower getBorrowerById(Long id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Borrower not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    public List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }

    public BorrowerResponse toResponse(Borrower borrower) {
        return BorrowerResponse.builder()
                .id(borrower.getId())
                .name(borrower.getName())
                .email(borrower.getEmail())
                .createdAt(borrower.getCreatedAt())
                .build();
    }
}
