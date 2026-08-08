import { clerkMiddleware } from "@clerk/nextjs/server";
import { NextResponse } from "next/server";

const hasClerkKeys = Boolean(
  process.env.CLERK_SECRET_KEY && process.env.NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY
);

// Route protection for /admin and /operator is enforced today by
// components/shared/RouteGuard.tsx on the client (mock-auth mode).
// Once Clerk keys are present, this proxy takes over server-side
// enforcement automatically -- see auth().protect() usage you'd add here,
// e.g. checking sessionClaims.metadata.role against the requested segment.
//
// NOTE: as of Next.js 16, `middleware.ts` is deprecated in favor of
// `proxy.ts` (file renamed, exported function renamed middleware -> proxy).
// See node_modules/next/dist/docs/01-app/02-guides/upgrading/version-16.md.
export default hasClerkKeys
  ? clerkMiddleware()
  : function proxy() {
      return NextResponse.next();
    };

export const config = {
  matcher: ["/((?!_next|.*\\..*).*)"],
};
