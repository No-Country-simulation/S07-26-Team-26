import { redirect } from "next/navigation";

// Clerk's NEXT_PUBLIC_CLERK_SIGN_IN_URL points here. Project Ghost Load's
// actual sign-in experience is the unified login screen at "/login", so
// this route just forwards there. Kept as its own catch-all route (rather
// than deleted) so Clerk's redirect config has a stable target regardless
// of which flow ends up needing a dedicated Clerk <SignIn /> mounting point
// later on.
export default function SignInPage() {
  redirect("/login");
}
