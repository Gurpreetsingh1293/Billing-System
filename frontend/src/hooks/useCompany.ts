import { useQuery } from '@tanstack/react-query';
import { api } from '../lib/api';

export interface CompanyInfo {
  id: string;
  name: string;
  address: string;
  gstin: string;
  state: string;
  phone: string;
}

export const useCompany = () => {
  return useQuery<CompanyInfo>({
    queryKey: ['company', 'me'],
    queryFn: async () => {
      const { data } = await api.get<CompanyInfo>('/company/me');
      return data;
    },
  });
};
