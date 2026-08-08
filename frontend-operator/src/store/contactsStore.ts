// ---------------------------------------------------------------------------
// contactsStore
//
// Holds the admin's working set of contacts for the CSV-import flow on
// /admin/contacts. Starts seeded from mock/contacts.json (as if a prior
// campaign already imported a small list) and grows as CSVs are parsed
// client-side. "Preparing a campaign" is mocked here; once a backend
// exists this becomes a POST that creates a Campaign from the valid rows.
// ---------------------------------------------------------------------------
import { create } from "zustand";
import contactsSeed from "@/mock/contacts.json";

export type ContactStatus = "invited" | "started" | "completed";

export interface Contact {
  id: string;
  name: string;
  email: string;
  company: string;
  status: ContactStatus;
  valid: boolean;
  issue?: string;
}

interface ContactsState {
  contacts: Contact[];
  addContacts: (rows: Omit<Contact, "id" | "status">[]) => void;
  reset: () => void;
}

let importCounter = 0;

export const useContactsStore = create<ContactsState>()((set) => ({
  contacts: contactsSeed as Contact[],
  addContacts: (rows) =>
    set((state) => ({
      contacts: [
        ...state.contacts,
        ...rows.map((row) => {
          importCounter += 1;
          return { ...row, id: `import-${importCounter}`, status: "invited" as ContactStatus };
        }),
      ],
    })),
  reset: () => set({ contacts: contactsSeed as Contact[] }),
}));
