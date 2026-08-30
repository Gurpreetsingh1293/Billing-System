package com.example.demo.invoice;

import com.example.demo.domain.Invoice;

public interface GSPClient {
    
    /**
     * Submits an invoice to the GST Suvidha Provider to generate an IRN.
     * @param invoice The invoice to submit
     * @return The generated IRN and QR code
     * @throws RuntimeException if the submission fails
     */
    GSPResponse submitInvoice(Invoice invoice);

    class GSPResponse {
        private String irn;
        private String qrCode;

        public GSPResponse(String irn, String qrCode) {
            this.irn = irn;
            this.qrCode = qrCode;
        }

        public String getIrn() { return irn; }
        public String getQrCode() { return qrCode; }
    }
}
