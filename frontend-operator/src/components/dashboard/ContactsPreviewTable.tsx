"use client";

import { Card, CardContent } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { useContactsStore, type ContactStatus } from "@/store/contactsStore";

const statusTone: Record<ContactStatus, "neutral" | "warning" | "success"> = {
  invited: "neutral",
  started: "warning",
  completed: "success",
};

const statusLabel: Record<ContactStatus, string> = {
  invited: "Invited",
  started: "Started",
  completed: "Completed",
};

export function ContactsPreviewTable() {
  const contacts = useContactsStore((s) => s.contacts);

  return (
    <Card>
      <CardContent className="p-6">
        <p className="mb-1 flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-gold-700">
          <span className="h-px w-4 bg-gold-500" />
          Preview
        </p>
        <h2 className="mb-5 text-lg font-semibold text-graphite-900">Imported contacts</h2>

        <div className="overflow-x-auto rounded-lg border border-graphite-100">
          <table className="w-full text-left text-sm">
            <thead className="bg-graphite-50 text-xs uppercase tracking-wide text-graphite-500">
              <tr>
                <th className="px-4 py-3 font-medium">Name</th>
                <th className="px-4 py-3 font-medium">Email</th>
                <th className="px-4 py-3 font-medium">Company</th>
                <th className="px-4 py-3 font-medium">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-graphite-100">
              {contacts.map((c) => (
                <tr key={c.id} className="hover:bg-graphite-50/60">
                  <td className="px-4 py-3 font-medium text-graphite-900">{c.name || "—"}</td>
                  <td className="px-4 py-3 text-graphite-600">{c.email || "—"}</td>
                  <td className="px-4 py-3 text-graphite-600">{c.company || "—"}</td>
                  <td className="px-4 py-3">
                    {c.valid ? (
                      <Badge tone={statusTone[c.status]}>{statusLabel[c.status]}</Badge>
                    ) : (
                      <Badge tone="danger" title={c.issue}>
                        Needs review
                      </Badge>
                    )}
                  </td>
                </tr>
              ))}
              {contacts.length === 0 && (
                <tr>
                  <td colSpan={4} className="px-4 py-8 text-center text-sm text-graphite-400">
                    No contacts imported yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  );
}
