package com.example.library.service;

import com.example.library.dto.BorrowerResponse;
import com.example.library.dto.RegisterBorrower;
import com.example.library.exception.CustomException;
import com.example.library.model.Borrower;
import com.example.library.repository.BorrowerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerService borrowerService;

    @Test
    void registerBorrower_Success() {
        Borrower borrower = Borrower.builder()
                        .id(1L)
                        .name("Amidu")
                        .email("amidu@example.com")
                        .createdAt(LocalDateTime.now())
                        .build();
        RegisterBorrower request = new RegisterBorrower();
        request.setName("Amidu");
        request.setEmail("amidu@example.com");

        when(borrowerRepository.existsByEmail(anyString())).thenReturn(false);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);

        BorrowerResponse response = borrowerService.registerBorrower(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Amidu");
        assertThat(response.getEmail()).isEqualTo("amidu@example.com");
        verify(borrowerRepository).save(any(Borrower.class));
    }

    @Test
    void getBorrowerById_found() {
        Borrower borrower = Borrower.builder()
                .id(1L)
                .name("Amidu")
                .email("amidu@example.com")
                .createdAt(LocalDateTime.now())
                .build();
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));

        Borrower result = borrowerService.getBorrowerById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test

    void getBorrowerById_NotFound() {
        when(borrowerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowerService.getBorrowerById(99L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("99");
    }
}
