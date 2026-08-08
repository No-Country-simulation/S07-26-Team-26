"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore, type Role } from "@/store/authStore";

// Client-side guard for the mock-auth path. Once Clerk is fully wired,
// prefer enforcing this in middleware.ts using Clerk's auth() helper --
// this component stays useful as a defense-in-depth / instant redirect
// while the server round-trip resolves.
export function RouteGuard({
  role,
  children,
}: {
  role: Role | Role[];
  children: React.ReactNode;
}) {
  const session = useAuthStore((s) => s.session);
  const router = useRouter();
  const allowed = Array.isArray(role) ? role : [role];
  const isAllowed = Boolean(session && allowed.includes(session.role));

  useEffect(() => {
    if (!isAllowed) {
      router.replace("/login");
    }
  }, [isAllowed, router]);

  if (!isAllowed) {
    return (
      <div className="flex h-screen items-center justify-center text-sm text-graphite-400">
        Redirecting…
      </div>
    );
  }

  return <>{children}</>;
}
