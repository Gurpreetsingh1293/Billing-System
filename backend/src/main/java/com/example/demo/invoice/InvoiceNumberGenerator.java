package com.example.demo.invoice;

import com.example.demo.domain.Company;
import com.example.demo.domain.InvoiceSequence;
import com.example.demo.repository.InvoiceSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class InvoiceNumberGenerator {

    private final InvoiceSequenceRepository sequenceRepository;

    public InvoiceNumberGenerator(InvoiceSequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateNextInvoiceNumber(Company company) {
        String financialYear = getCurrentFinancialYear();
        UUID tenantId = company.getId();

        InvoiceSequence sequence = sequenceRepository.findByTenantIdAndFinancialYearForUpdate(tenantId, financialYear)
                .orElseGet(() -> {
                    InvoiceSequence newSeq = new InvoiceSequence();
                    newSeq.setTenantId(tenantId);
                    newSeq.setFinancialYear(financialYear);
                    newSeq.setCurrentValue(0L);
                    return newSeq;
                });

        long nextValue = sequence.getCurrentValue() + 1;
        sequence.setCurrentValue(nextValue);
        sequenceRepository.save(sequence);

        return formatInvoiceNumber(company.getInvoiceNumberFormat(), financialYear, nextValue);
    }

    private String getCurrentFinancialYear() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        // Indian Financial Year starts in April
        if (today.getMonthValue() < 4) {
            return (year - 1) + "-" + String.format("%02d", year % 100);
        } else {
            return year + "-" + String.format("%02d", (year + 1) % 100);
        }
    }

    private String formatInvoiceNumber(String formatTemplate, String financialYear, long sequenceNumber) {
        if (formatTemplate == null || formatTemplate.isEmpty()) {
            formatTemplate = "INV-{YYYY}-{SEQ}";
        }
        String seqString = String.format("%04d", sequenceNumber);
        return formatTemplate
                .replace("{YYYY}", financialYear)
                .replace("{SEQ}", seqString);
    }
}
