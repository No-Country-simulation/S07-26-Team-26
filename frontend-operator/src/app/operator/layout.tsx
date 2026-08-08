"use client";

import { InvitationGuard } from "@/components/shared/InvitationGuard";
import { OperatorTopbar } from "@/components/navbar/OperatorTopbar";

export default function OperatorLayout({ children }: { children: React.ReactNode }) {
  return (
    <InvitationGuard>
      <div className="min-h-screen bg-[#F7F9F8]">
        <OperatorTopbar />
        <main className="mx-auto max-w-4xl px-6 py-10">{children}</main>
      </div>
    </InvitationGuard>
  );
}
