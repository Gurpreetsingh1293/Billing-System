import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useInvoices, useIssueInvoice, downloadInvoicePdf } from '../hooks/useInvoices';
import type { InvoiceResponse } from '../hooks/useInvoices';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import './InvoiceDetail.css';

const statusLabel = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: 'Draft',
    ISSUED: 'Issued',
    PENDING_IRN: 'Pending IRN',
    PAID: 'Paid',
  };
  return map[status] || status;
};

const formatCurrency = (val: number | null | undefined) =>
  `₹${(val ?? 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

const formatDate = (iso: string | null | undefined) => {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
};

export const InvoiceDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: invoices, isLoading } = useInvoices();
  const { mutateAsync: issueInvoice, isPending: isIssuing } = useIssueInvoice();

  if (isLoading) {
    return <div className="detail-container">Loading...</div>;
  }

  const invoice: InvoiceResponse | undefined = invoices?.find(inv => inv.id === id);

  if (!invoice) {
    return (
      <div className="detail-container">
        <h2>Invoice not found</h2>
        <Button variant="secondary" onClick={() => navigate('/')}>Back to Dashboard</Button>
      </div>
    );
  }

  const handleIssue = async () => {
    try {
      await issueInvoice(invoice.id);
    } catch {
      alert('Failed to issue invoice');
    }
  };

  const handleDownloadPdf = () => {
    downloadInvoicePdf(invoice.id, invoice.invoiceNumber);
  };

  const subtotal = (invoice.totalAmount ?? 0) - (invoice.cgstAmount ?? 0) - (invoice.sgstAmount ?? 0) - (invoice.igstAmount ?? 0);

  return (
    <div className="detail-container">
      <div className="detail-header">
        <div className="detail-header-left">
          <h2>{invoice.invoiceNumber || 'DRAFT'}</h2>
          <span className={`status-badge status-badge--${invoice.status.toLowerCase()}`}>
            {statusLabel(invoice.status)}
          </span>
        </div>
        <div className="detail-actions">
          {invoice.status === 'DRAFT' && (
            <Button onClick={handleIssue} isLoading={isIssuing}>Issue Invoice</Button>
          )}
          {(invoice.status === 'ISSUED' || invoice.status === 'PAID') && (
            <Button onClick={handleDownloadPdf}>Download PDF</Button>
          )}
          <Button variant="secondary" onClick={() => navigate('/')}>Back to Dashboard</Button>
        </div>
      </div>

      <Card className="detail-section glass-panel">
        <h3>Details</h3>
        <div className="detail-meta">
          <p><strong>Client:</strong> {invoice.clientName || '—'}</p>
          <p><strong>Created:</strong> {formatDate(invoice.createdAt)}</p>
          {invoice.issuedAt && <p><strong>Issued:</strong> {formatDate(invoice.issuedAt)}</p>}
          {invoice.irn && <p><strong>IRN:</strong> {invoice.irn}</p>}
        </div>
      </Card>

      <Card className="detail-section glass-panel">
        <h3>Tax Breakdown</h3>
        <div className="detail-totals" style={{ width: '100%', margin: 0, padding: 0 }}>
          <div className="row">
            <span>Subtotal</span>
            <span>{formatCurrency(subtotal)}</span>
          </div>
          {(invoice.cgstAmount ?? 0) > 0 && (
            <div className="row">
              <span>CGST (9%)</span>
              <span>{formatCurrency(invoice.cgstAmount)}</span>
            </div>
          )}
          {(invoice.sgstAmount ?? 0) > 0 && (
            <div className="row">
              <span>SGST (9%)</span>
              <span>{formatCurrency(invoice.sgstAmount)}</span>
            </div>
          )}
          {(invoice.igstAmount ?? 0) > 0 && (
            <div className="row">
              <span>IGST (18%)</span>
              <span>{formatCurrency(invoice.igstAmount)}</span>
            </div>
          )}
          <hr className="divider" />
          <div className="row grand">
            <span>Grand Total</span>
            <span>{formatCurrency(invoice.totalAmount)}</span>
          </div>
        </div>
      </Card>
    </div>
  );
};
