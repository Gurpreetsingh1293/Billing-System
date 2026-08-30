import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { api } from '../lib/api';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Card } from '../components/ui/Card';
import './Login.css';

export const Login: React.FC = () => {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('password');
  const [tenantId, setTenantId] = useState('11111111-1111-1111-1111-111111111111');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const setToken = useAuthStore(state => state.setToken);
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    
    try {
      const response = await api.post('/auth/login', 
        { username, password },
        { headers: { 'X-Tenant-ID': tenantId } }
      );
      // In MVP, backend returns AuthResponse { token: string }
      setToken(response.data.token, tenantId);
      navigate('/');
    } catch (err) {
      setError('Invalid credentials');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-container">
      <Card className="login-card glass-panel">
        <h2 className="login-title">Invoice SaaS</h2>
        <p className="login-subtitle">Sign in to your account</p>
        
        {error && <div className="login-error">{error}</div>}
        
        <form onSubmit={handleLogin} className="flex flex-col">
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.3rem', marginBottom: '1rem' }}>
            <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--color-text-secondary)', textTransform: 'uppercase' }}>Company</label>
            <select 
              value={tenantId}
              onChange={e => setTenantId(e.target.value)}
              style={{
                width: '100%',
                padding: '0.5rem 0.75rem',
                border: '1px solid var(--color-border)',
                borderRadius: 'var(--radius-md)',
                fontSize: '0.9rem',
                background: 'var(--color-bg-surface)',
                color: 'var(--color-text-primary)'
              }}
            >
              <option value="11111111-1111-1111-1111-111111111111">AIR KING EQUIPMENT (Indore)</option>
              <option value="aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa">AIR KING (Bhavnagar)</option>
            </select>
          </div>
          <Input 
            label="Username" 
            value={username} 
            onChange={(e) => setUsername(e.target.value)} 
            required 
          />
          <Input 
            label="Password" 
            type="password" 
            value={password} 
            onChange={(e) => setPassword(e.target.value)} 
            required 
          />
          <Button type="submit" className="login-btn" isLoading={isLoading}>
            Sign In
          </Button>
        </form>
      </Card>
    </div>
  );
};
