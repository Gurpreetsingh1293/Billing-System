import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';

export interface InvoiceLineItemRequest {
  productId: string;
  quantity: number;
  rate: number;
}

export interface InvoiceRequest {
  clientId: string;
  items: InvoiceLineItemRequest[];
}

export interface InvoiceResponse {
  id: string;
  invoiceNumber: string | null;
  status: string;
  clientName: string | null;
  totalAmount: number;
  cgstAmount: number;
  sgstAmount: number;
  igstAmount: number;
  createdAt: string | null;
  issuedAt: string | null;
  irn: string | null;
}

export const useInvoices = () => {
  return useQuery<InvoiceResponse[]>({
    queryKey: ['invoices'],
    queryFn: async () => {
      const { data } = await api.get<InvoiceResponse[]>('/invoices');
      return data;
    },
  });
};

export const useCreateDraftInvoice = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (invoiceReq: InvoiceRequest) => {
      const { data } = await api.post<InvoiceResponse>('/invoices/draft', invoiceReq);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['invoices'] });
    },
  });
};

export const useIssueInvoice = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      const { data } = await api.post<InvoiceResponse>(`/invoices/${id}/issue`);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['invoices'] });
    },
  });
};

export const downloadInvoicePdf = async (id: string, invoiceNumber?: string | null) => {
  const response = await api.get(`/invoices/${id}/pdf`, { responseType: 'blob' });
  const blob = new Blob([response.data], { type: 'application/pdf' });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `invoice-${invoiceNumber || id}.pdf`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};
