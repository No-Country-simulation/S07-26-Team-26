import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { ProfileForm } from "@/components/forms/ProfileForm";

export default function OperatorProfilePage() {
  return (
    <div>
      <div className="mb-6">
        <h1 className="text-xl font-semibold tracking-tight text-graphite-900">Tell us about your facility</h1>
        <p className="mt-1 text-sm text-graphite-500">
          This context calibrates your benchmark against comparable operators.
        </p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Company Profile</CardTitle>
        </CardHeader>
        <CardContent>
          <ProfileForm />
        </CardContent>
      </Card>
    </div>
  );
}
