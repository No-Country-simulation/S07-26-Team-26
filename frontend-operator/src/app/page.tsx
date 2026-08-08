import { redirect } from "next/navigation";

// No login screen for the Operator anymore -- they always arrive via
// /invitation/{invitationToken}. Until the real invitation emails/backend
// exist, "/" forwards to a demo invitation token so the flow can be tested
// and shown end-to-end (mock mode fabricates a plausible invitation for any
// token). Swap this for a real per-operator link once GET
// /api/v1/invitations/{invitationToken} is live.
export default function RootPage() {
  redirect("/invitation/demo");
}
