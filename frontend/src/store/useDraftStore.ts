import { create } from 'zustand';
import { openDB } from 'idb';

interface DraftInvoice {
  id: string;
  clientId: string | null;
  lineItems: any[];
  updatedAt: number;
}

interface DraftState {
  drafts: Record<string, DraftInvoice>;
  initDB: () => Promise<void>;
  saveDraft: (draft: DraftInvoice) => Promise<void>;
  loadDrafts: () => Promise<void>;
  deleteDraft: (id: string) => Promise<void>;
}

const DB_NAME = 'invoice-saas-db';
const STORE_NAME = 'drafts';

export const useDraftStore = create<DraftState>((set, get) => ({
  drafts: {},
  initDB: async () => {
    await openDB(DB_NAME, 1, {
      upgrade(db) {
        if (!db.objectStoreNames.contains(STORE_NAME)) {
          db.createObjectStore(STORE_NAME, { keyPath: 'id' });
        }
      },
    });
    await get().loadDrafts();
  },
  saveDraft: async (draft) => {
    const db = await openDB(DB_NAME, 1);
    await db.put(STORE_NAME, draft);
    set((state) => ({ drafts: { ...state.drafts, [draft.id]: draft } }));
  },
  loadDrafts: async () => {
    const db = await openDB(DB_NAME, 1);
    const allDrafts = await db.getAll(STORE_NAME);
    const draftsRecord = allDrafts.reduce((acc, curr) => {
      acc[curr.id] = curr;
      return acc;
    }, {} as Record<string, DraftInvoice>);
    set({ drafts: draftsRecord });
  },
  deleteDraft: async (id) => {
    const db = await openDB(DB_NAME, 1);
    await db.delete(STORE_NAME, id);
    set((state) => {
      const newDrafts = { ...state.drafts };
      delete newDrafts[id];
      return { drafts: newDrafts };
    });
  }
}));
