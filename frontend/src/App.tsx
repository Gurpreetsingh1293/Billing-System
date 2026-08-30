import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/useAuthStore';
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { InvoiceBuilder } from './pages/InvoiceBuilder';
import { InvoiceDetail } from './pages/InvoiceDetail';
import { ClientList } from './pages/ClientList';

const PrivateRoute = ({ children }: { children: React.ReactNode }) => {
  const token = useAuthStore(state => state.token);
  return token ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route 
          path="/" 
          element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/invoices/new" 
          element={
            <PrivateRoute>
              <InvoiceBuilder />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/invoices/:id" 
          element={
            <PrivateRoute>
              <InvoiceDetail />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/clients" 
          element={
            <PrivateRoute>
              <ClientList />
            </PrivateRoute>
          } 
        />
      </Routes>
    </Router>
  );
}

export default App;
