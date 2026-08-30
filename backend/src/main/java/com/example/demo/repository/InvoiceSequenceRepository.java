package com.example.demo.repository;

import com.example.demo.domain.InvoiceSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequence, UUID> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InvoiceSequence s WHERE s.tenantId = :tenantId AND s.financialYear = :financialYear")
    Optional<InvoiceSequence> findByTenantIdAndFinancialYearForUpdate(
            @Param("tenantId") UUID tenantId, 
            @Param("financialYear") String financialYear);
}
