"use client";

import { useState } from "react";
import { Card, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { useContactsStore } from "@/store/contactsStore";
import { usePrepareCampaign } from "@/hooks/useContacts";

export function ContactsValidationCard() {
  const contacts = useContactsStore((s) => s.contacts);
  const prepareCampaign = usePrepareCampaign();
  const [confirmation, setConfirmation] = useState<string | null>(null);

  const available = contacts.length;
  const valid = contacts.filter((c) => c.valid).length;
  const needsReview = contacts.filter((c) => !c.valid).length;

  async function handlePrepare() {
    setConfirmation(null);
    const result = await prepareCampaign.mutateAsync({
      contactIds: contacts.filter((c) => c.valid).map((c) => c.id),
    });
    setConfirmation(`Campaign ${result.campaignId} prepared from ${valid} valid contacts.`);
  }

  return (
    <Card>
      <CardContent className="p-6">
        <p className="mb-5 flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-gold-700">
          <span className="h-px w-4 bg-gold-500" />
          Validation
        </p>

        <div className="space-y-5">
          <Stat value={available} label="Contacts available" />
          <div className="border-t border-graphite-100" />
          <Stat value={valid} label="Valid records" />
          <div className="border-t border-graphite-100" />
          <Stat value={needsReview} label="Needs review" tone={needsReview > 0 ? "warning" : "default"} />
        </div>

        <Button
          className="mt-6 w-full"
          disabled={valid === 0}
          loading={prepareCampaign.isPending}
          onClick={handlePrepare}
        >
          Prepare Campaign
        </Button>

        {confirmation && <p className="mt-3 text-xs font-medium text-forest-700">{confirmation}</p>}
      </CardContent>
    </Card>
  );
}

function Stat({ value, label, tone = "default" }: { value: number; label: string; tone?: "default" | "warning" }) {
  return (
    <div>
      <p className={`font-tabular text-3xl font-semibold ${tone === "warning" && value > 0 ? "text-orange-600" : "text-graphite-900"}`}>
        {value}
      </p>
      <p className="mt-0.5 text-sm text-graphite-500">{label}</p>
    </div>
  );
}
