"use client";

import { RouteGuard } from "@/components/shared/RouteGuard";
import { AdminSidebar } from "@/components/sidebar/AdminSidebar";

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <RouteGuard role={["ADMIN", "ROOT_ADMIN"]}>
      <div className="flex min-h-screen bg-[#F7F9F8]">
        <AdminSidebar />
        <div className="flex-1 overflow-y-auto">{children}</div>
      </div>
    </RouteGuard>
  );
}
