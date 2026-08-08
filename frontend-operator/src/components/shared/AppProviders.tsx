"use client";

import { ClerkProvider } from "@clerk/nextjs";
import QueryProvider from "@/providers/QueryProvider";

const publishableKey = process.env.NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY;

// Renders without ClerkProvider until Clerk keys exist, so the app runs in
// full mock-auth mode out of the box. Once NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY
// is set in .env.local, ClerkProvider activates automatically -- no code
// change required elsewhere.
export function AppProviders({ children }: { children: React.ReactNode }) {
  const content = <QueryProvider>{children}</QueryProvider>;

  if (!publishableKey) return content;

  return <ClerkProvider publishableKey={publishableKey}>{content}</ClerkProvider>;
}
