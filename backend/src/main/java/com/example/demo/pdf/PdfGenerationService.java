package com.example.demo.pdf;

import com.example.demo.domain.Invoice;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGenerationService {

    private final SpringTemplateEngine templateEngine;

    public PdfGenerationService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generateInvoicePdf(Invoice invoice, com.example.demo.domain.Company company) {
        Context context = new Context();
        context.setVariable("invoice", invoice);
        context.setVariable("company", company); 

        // Resolve per-tenant template; fall back to default
        String templateName = company.getTemplateName();
        String template = (templateName != null && !templateName.isBlank())
                ? "invoice-" + templateName
                : "invoice-template";

        String html = templateEngine.process(template, context);
        return generatePdfFromHtml(html);
    }

    public byte[] generateShippingLabelPdf(Invoice invoice) {
        Context context = new Context();
        context.setVariable("client", invoice.getClient());

        String html = templateEngine.process("shipping-label-template", context);
        return generatePdfFromHtml(html);
    }

    private byte[] generatePdfFromHtml(String html) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
