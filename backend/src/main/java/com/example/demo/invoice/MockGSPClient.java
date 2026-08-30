package com.example.demo.invoice;

import com.example.demo.domain.Invoice;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockGSPClient implements GSPClient {

    @Override
    public GSPResponse submitInvoice(Invoice invoice) {
        // Simulate an API call delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Generate fake IRN (64 char hex string) and QR code
        String fakeIrn = UUID.randomUUID().toString().replace("-", "") + 
                         UUID.randomUUID().toString().replace("-", "");
                         
        String fakeQr = "QR: " + invoice.getInvoiceNumber() + " | " + fakeIrn;
        
        return new GSPResponse(fakeIrn, fakeQr);
    }
}
