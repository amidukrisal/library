package com.example.library.repository;

import com.example.library.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {

    @Query("select br from BorrowRecord br where br.book.id = :bookId and br.returnedAt is null")
    Optional<BorrowRecord> findActiveRecordsByBookId(@Param("bookId") Long bookId);

    @Query("select br from BorrowRecord br where br.book.id = :bookId and br.borrower.id = :borrowerId and br.returnedAt is null")
    Optional<BorrowRecord> findByBookIdAndBorrowerId(
            @Param("bookId") Long bookId,
            @Param("borrowerId") Long borrowerId
    );
}
