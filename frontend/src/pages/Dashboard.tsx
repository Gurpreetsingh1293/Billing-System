import React, { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { useInvoices, downloadInvoicePdf } from '../hooks/useInvoices';
import type { InvoiceResponse } from '../hooks/useInvoices';
import { useCompany } from '../hooks/useCompany';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import './Dashboard.css';

const statusLabel = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: 'Draft',
    ISSUED: 'Issued',
    PENDING_IRN: 'Pending IRN',
    PAID: 'Paid',
  };
  return map[status] || status;
};

const formatCurrency = (val: number) =>
  `₹${val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

const formatDate = (iso: string | null | undefined) => {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
};

export const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const logout = useAuthStore(state => state.logout);
  const { data: invoices, isLoading } = useInvoices();
  const { data: company } = useCompany();

  const stats = useMemo(() => {
    if (!invoices) return { total: 0, revenue: 0, drafts: 0, issuedThisMonth: 0 };

    const now = new Date();
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);

    const total = invoices.length;
    const revenue = invoices
      .filter(inv => inv.status === 'ISSUED' || inv.status === 'PAID')
      .reduce((sum, inv) => sum + (inv.totalAmount ?? 0), 0);
    const drafts = invoices.filter(inv => inv.status === 'DRAFT').length;
    const issuedThisMonth = invoices.filter(inv => {
      if (!inv.issuedAt) return false;
      return new Date(inv.issuedAt) >= monthStart;
    }).length;

    return { total, revenue, drafts, issuedThisMonth };
  }, [invoices]);

  const handleDownload = (e: React.MouseEvent, inv: InvoiceResponse) => {
    e.stopPropagation();
    downloadInvoicePdf(inv.id, inv.invoiceNumber);
  };

  return (
    <div className="dashboard">
      {/* ── Header ── */}
      <header className="dashboard-header">
        <div className="dashboard-header-left">
          <h1>{company?.name || 'Invoice SaaS'}</h1>
          <p>{company?.gstin ? `GSTIN: ${company.gstin}` : 'Manage your invoices and billing'}</p>
        </div>
        <div className="dashboard-header-right">
          <Button onClick={() => navigate('/invoices/new')}>+ New Invoice</Button>
          <Button variant="secondary" onClick={() => navigate('/clients')}>Clients</Button>
          <Button variant="ghost" onClick={logout}>Logout</Button>
        </div>
      </header>

      {/* ── Stat Cards ── */}
      <div className="stat-grid">
        <Card className="stat-card glass-panel stat-card--total">
          <div className="stat-card__label">Total Invoices</div>
          <div className="stat-card__value">{isLoading ? '…' : stats.total}</div>
        </Card>
        <Card className="stat-card glass-panel stat-card--revenue">
          <div className="stat-card__label">Total Revenue</div>
          <div className="stat-card__value">{isLoading ? '…' : formatCurrency(stats.revenue)}</div>
        </Card>
        <Card className="stat-card glass-panel stat-card--drafts">
          <div className="stat-card__label">Drafts Pending</div>
          <div className="stat-card__value">{isLoading ? '…' : stats.drafts}</div>
        </Card>
        <Card className="stat-card glass-panel stat-card--month">
          <div className="stat-card__label">Issued This Month</div>
          <div className="stat-card__value">{isLoading ? '…' : stats.issuedThisMonth}</div>
        </Card>
      </div>

      {/* ── Invoice Table ── */}
      <Card className="table-section glass-panel">
        <div className="table-header">
          <h2>Recent Invoices</h2>
          {invoices && invoices.length > 0 && (
            <span className="count">{invoices.length} invoices</span>
          )}
        </div>

        {isLoading ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--color-text-muted)' }}>
            Loading invoices…
          </div>
        ) : !invoices || invoices.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state__icon">📄</div>
            <h3>No invoices yet</h3>
            <p>Create your first invoice to get started with billing and tracking.</p>
            <Button onClick={() => navigate('/invoices/new')}>Create First Invoice</Button>
          </div>
        ) : (
          <table className="invoice-table">
            <thead>
              <tr>
                <th>Invoice #</th>
                <th>Client</th>
                <th>Status</th>
                <th>Date</th>
                <th style={{ textAlign: 'right' }}>Amount</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {invoices.map(inv => (
                <tr key={inv.id} onClick={() => navigate(`/invoices/${inv.id}`)}>
                  <td className={`col-number ${!inv.invoiceNumber ? 'col-number--draft' : ''}`}>
                    {inv.invoiceNumber || '— Draft —'}
                  </td>
                  <td>{inv.clientName || '—'}</td>
                  <td>
                    <span className={`status-badge status-badge--${inv.status.toLowerCase()}`}>
                      {statusLabel(inv.status)}
                    </span>
                  </td>
                  <td className="col-date">{formatDate(inv.issuedAt || inv.createdAt)}</td>
                  <td className="col-amount">{formatCurrency(inv.totalAmount ?? 0)}</td>
                  <td>
                    <div className="table-actions">
                      <button
                        className="action-link"
                        onClick={(e) => { e.stopPropagation(); navigate(`/invoices/${inv.id}`); }}
                      >
                        View
                      </button>
                      {(inv.status === 'ISSUED' || inv.status === 'PAID') && (
                        <button className="action-link" onClick={(e) => handleDownload(e, inv)}>
                          PDF
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  );
};
