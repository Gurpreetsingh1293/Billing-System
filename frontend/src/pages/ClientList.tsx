import React, { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useClients, useCreateClient } from '../hooks/useClients';
import type { Client } from '../hooks/useClients';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import './ClientList.css';

const ClientFormModal: React.FC<{
  onClose: () => void;
  onSave: (client: Omit<Client, 'id'>) => Promise<void>;
  isSaving: boolean;
}> = ({ onClose, onSave, isSaving }) => {
  const [form, setForm] = useState({
    name: '',
    billingAddress: '',
    shippingAddress: '',
    gstin: '',
    state: '',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSave(form);
    onClose();
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content glass-panel card" onClick={(e: React.MouseEvent) => e.stopPropagation()}>
        <h2>Add New Client</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <label>Client Name *</label>
            <input
              required
              value={form.name}
              onChange={e => setForm({ ...form, name: e.target.value })}
              placeholder="e.g. BIOPAC VENTURES PVT LTD"
            />
          </div>
          <div className="form-row">
            <label>GSTIN</label>
            <input
              value={form.gstin}
              onChange={e => setForm({ ...form, gstin: e.target.value })}
              placeholder="e.g. 24AAJCB8370J1ZE"
              maxLength={15}
            />
          </div>
          <div className="form-row">
            <label>State *</label>
            <input
              required
              value={form.state}
              onChange={e => setForm({ ...form, state: e.target.value })}
              placeholder="e.g. Gujarat"
            />
          </div>
          <div className="form-row">
            <label>Billing Address</label>
            <textarea
              value={form.billingAddress}
              onChange={e => setForm({ ...form, billingAddress: e.target.value })}
              placeholder="Full billing address"
            />
          </div>
          <div className="form-row">
            <label>Shipping Address</label>
            <textarea
              value={form.shippingAddress}
              onChange={e => setForm({ ...form, shippingAddress: e.target.value })}
              placeholder="Leave blank if same as billing"
            />
          </div>
          <div className="form-actions">
            <Button variant="secondary" type="button" onClick={onClose}>Cancel</Button>
            <Button type="submit" isLoading={isSaving}>Save Client</Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export const ClientList: React.FC = () => {
  const navigate = useNavigate();
  const { data: clients, isLoading } = useClients();
  const { mutateAsync: createClient, isPending: isSaving } = useCreateClient();
  const [search, setSearch] = useState('');
  const [showForm, setShowForm] = useState(false);

  const filtered = useMemo(() => {
    if (!clients) return [];
    if (!search.trim()) return clients;
    const q = search.toLowerCase();
    return clients.filter(c =>
      c.name.toLowerCase().includes(q) ||
      (c.gstin && c.gstin.toLowerCase().includes(q)) ||
      c.state.toLowerCase().includes(q)
    );
  }, [clients, search]);

  const handleSave = async (client: Omit<Client, 'id'>) => {
    await createClient(client);
  };

  return (
    <div className="client-page">
      <div className="client-page-header">
        <h1>Client Directory</h1>
        <div className="flex gap-md">
          <Button onClick={() => setShowForm(true)}>+ Add Client</Button>
          <Button variant="secondary" onClick={() => navigate('/')}>Back to Dashboard</Button>
        </div>
      </div>

      <div className="client-search">
        <input
          type="text"
          placeholder="Search by name, GSTIN, or state..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>

      <Card className="glass-panel" style={{ padding: 0, overflow: 'hidden' }}>
        {isLoading ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--color-text-muted)' }}>
            Loading clients…
          </div>
        ) : filtered.length === 0 ? (
          <div className="client-empty">
            <h3>{search ? 'No matching clients' : 'No clients yet'}</h3>
            <p>
              {search
                ? 'Try a different search term.'
                : 'Add your first client so you never have to re-type their details.'}
            </p>
            {!search && <Button onClick={() => setShowForm(true)}>Add First Client</Button>}
          </div>
        ) : (
          <table className="client-table">
            <thead>
              <tr>
                <th>Client Name</th>
                <th>GSTIN</th>
                <th>State</th>
                <th>Billing Address</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(client => (
                <tr key={client.id}>
                  <td className="client-name">{client.name}</td>
                  <td className="gstin-cell">{client.gstin || '—'}</td>
                  <td>{client.state}</td>
                  <td>{client.billingAddress || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>

      {showForm && (
        <ClientFormModal
          onClose={() => setShowForm(false)}
          onSave={handleSave}
          isSaving={isSaving}
        />
      )}
    </div>
  );
};
