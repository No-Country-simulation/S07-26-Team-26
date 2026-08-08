"use client";

import { AdminTopbar } from "@/components/navbar/AdminTopbar";
import { CsvUploadCard } from "@/components/forms/CsvUploadCard";
import { ContactsValidationCard } from "@/components/dashboard/ContactsValidationCard";
import { ContactsPreviewTable } from "@/components/dashboard/ContactsPreviewTable";

export default function AdminContactsPage() {
  return (
    <>
      <AdminTopbar title="Contact Base" description="Import a contact list and validate it before creating a campaign" />
      <div className="space-y-6 p-8">
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-5">
          <div className="lg:col-span-3">
            <CsvUploadCard />
          </div>
          <div className="lg:col-span-2">
            <ContactsValidationCard />
          </div>
        </div>

        <ContactsPreviewTable />
      </div>
    </>
  );
}
