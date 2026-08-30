import React, { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { useClients } from '../hooks/useClients';
import { useProducts } from '../hooks/useProducts';
import { useCreateDraftInvoice } from '../hooks/useInvoices';
import './InvoiceBuilder.css';

interface LineItemState {
  id: string; // purely for UI keying
  productId: string;
  quantity: number;
  rate: number;
}

export const InvoiceBuilder: React.FC = () => {
  const navigate = useNavigate();
  const { data: clients, isLoading: clientsLoading } = useClients();
  const { data: products, isLoading: productsLoading } = useProducts();
  const { mutateAsync: createDraft, isPending: isSaving } = useCreateDraftInvoice();

  const [clientId, setClientId] = useState('');
  const [lineItems, setLineItems] = useState<LineItemState[]>([
    { id: Date.now().toString(), productId: '', quantity: 1, rate: 0 }
  ]);
  const [error, setError] = useState('');

  // Auto-fill rate when product changes
  const handleProductChange = (index: number, newProductId: string) => {
    const product = products?.find(p => p.id === newProductId);
    const newItems = [...lineItems];
    newItems[index].productId = newProductId;
    if (product) {
      newItems[index].rate = product.defaultSaleRate;
    }
    setLineItems(newItems);
  };

  const handleLineItemChange = (index: number, field: keyof LineItemState, value: string | number) => {
    const newItems = [...lineItems];
    newItems[index] = { ...newItems[index], [field]: value };
    setLineItems(newItems);
  };

  const addLineItem = () => {
    setLineItems([...lineItems, { id: Date.now().toString(), productId: '', quantity: 1, rate: 0 }]);
  };

  const removeLineItem = (index: number) => {
    if (lineItems.length > 1) {
      setLineItems(lineItems.filter((_, i) => i !== index));
    }
  };

  // Client-side calculations for realtime feedback
  const totals = useMemo(() => {
    let subtotal = 0;
    lineItems.forEach(item => {
      subtotal += item.quantity * item.rate;
    });

    // MVP assumes 18% GST. The real API will calculate properly based on states.
    const tax = subtotal * 0.18;
    const grandTotal = subtotal + tax;

    return { subtotal, tax, grandTotal };
  }, [lineItems]);

  const handleSaveDraft = async () => {
    if (!clientId) {
      setError('Please select a client');
      return;
    }
    if (lineItems.some(item => !item.productId || item.quantity <= 0)) {
      setError('Please ensure all line items have a product and valid quantity');
      return;
    }

    try {
      setError('');
      await createDraft({
        clientId,
        items: lineItems.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          rate: item.rate
        }))
      });
      navigate('/');
    } catch (err) {
      setError('Failed to save draft invoice');
    }
  };

  if (clientsLoading || productsLoading) {
    return <div className="container" style={{ padding: '2rem' }}>Loading...</div>;
  }

  return (
    <div className="container" style={{ padding: '2rem 1rem' }}>
      <header className="flex items-center justify-between" style={{ marginBottom: '2rem' }}>
        <h2>Create Invoice</h2>
        <Button variant="secondary" onClick={() => navigate('/')}>Cancel</Button>
      </header>

      {error && <div className="error-banner">{error}</div>}

      <Card className="glass-panel" style={{ marginBottom: '2rem' }}>
        <h3>Client Details</h3>
        <div className="form-group" style={{ maxWidth: '400px', marginTop: '1rem' }}>
          <label>Select Client</label>
          <select 
            className="input-field" 
            value={clientId} 
            onChange={(e) => setClientId(e.target.value)}
            style={{ width: '100%' }}
          >
            <option value="">-- Select Client --</option>
            {clients?.map(client => (
              <option key={client.id} value={client.id}>{client.name} - {client.gstin}</option>
            ))}
          </select>
        </div>
      </Card>

      <Card className="glass-panel line-items-card">
        <h3>Line Items</h3>
        <table className="line-items-table">
          <thead>
            <tr>
              <th>Product</th>
              <th style={{ width: '100px' }}>Quantity</th>
              <th style={{ width: '150px' }}>Rate</th>
              <th style={{ width: '150px' }}>Amount</th>
              <th style={{ width: '80px' }}></th>
            </tr>
          </thead>
          <tbody>
            {lineItems.map((item, index) => (
              <tr key={item.id}>
                <td>
                  <select 
                    className="input-field"
                    value={item.productId}
                    onChange={(e) => handleProductChange(index, e.target.value)}
                    style={{ width: '100%' }}
                  >
                    <option value="">-- Select Product --</option>
                    {products?.map(product => (
                      <option key={product.id} value={product.id}>{product.name}</option>
                    ))}
                  </select>
                </td>
                <td>
                  <Input 
                    type="number" 
                    min="1" 
                    value={item.quantity} 
                    onChange={(e) => handleLineItemChange(index, 'quantity', parseFloat(e.target.value) || 0)}
                    style={{ marginBottom: 0 }}
                  />
                </td>
                <td>
                  <Input 
                    type="number" 
                    min="0" 
                    step="0.01" 
                    value={item.rate} 
                    onChange={(e) => handleLineItemChange(index, 'rate', parseFloat(e.target.value) || 0)}
                    style={{ marginBottom: 0 }}
                  />
                </td>
                <td className="amount-cell">
                  ₹{(item.quantity * item.rate).toFixed(2)}
                </td>
                <td>
                  <Button 
                    variant="danger" 
                    size="sm" 
                    onClick={() => removeLineItem(index)}
                    disabled={lineItems.length === 1}
                  >
                    X
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
        <Button variant="secondary" onClick={addLineItem} style={{ marginTop: '1rem' }}>
          + Add Line Item
        </Button>
      </Card>

      <div className="flex justify-between" style={{ marginTop: '2rem', alignItems: 'flex-start' }}>
        <div className="action-bar flex gap-md">
          <Button onClick={handleSaveDraft} isLoading={isSaving}>Save Draft</Button>
        </div>

        <Card className="totals-panel glass-panel">
          <div className="total-row">
            <span>Subtotal:</span>
            <span>₹{totals.subtotal.toFixed(2)}</span>
          </div>
          <div className="total-row">
            <span>Estimated GST (18%):</span>
            <span>₹{totals.tax.toFixed(2)}</span>
          </div>
          <hr style={{ margin: '1rem 0', borderColor: 'var(--color-border)' }} />
          <div className="total-row grand-total">
            <span>Total:</span>
            <span>₹{totals.grandTotal.toFixed(2)}</span>
          </div>
          <p className="totals-hint">*Final exact GST split (CGST/SGST vs IGST) is calculated by the server upon saving.</p>
        </Card>
      </div>
    </div>
  );
};
