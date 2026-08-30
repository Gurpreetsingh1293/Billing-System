import { useQuery } from '@tanstack/react-query';
import { api } from '../lib/api';

export interface Product {
  id: string;
  name: string;
  hsnCode: string;
  unit: string;
  defaultSaleRate: number;
}

export const useProducts = () => {
  return useQuery({
    queryKey: ['products'],
    queryFn: async () => {
      const { data } = await api.get<Product[]>('/products');
      return data;
    },
  });
};
