"use client";

import { useState } from "react";
import { AdminTopbar } from "@/components/navbar/AdminTopbar";
import { RouteGuard } from "@/components/shared/RouteGuard";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { Label } from "@/components/ui/Label";
import { Input } from "@/components/ui/Input";
import { Button } from "@/components/ui/Button";

export default function SettingsPage() {
  return (
    <RouteGuard role="ROOT_ADMIN">
      <SettingsPageContent />
    </RouteGuard>
  );
}

// UI-only in this demo -- nothing here is persisted or wired into the
// scoring engine yet. Once a backend exists, "Save" posts to
// `${API_BASE_URL}/settings` and lib/scoring.ts reads thresholds from
// there instead of the hard-coded values in maturityLevelFromScore().
function SettingsPageContent() {
  const [saved, setSaved] = useState(false);
  const [thresholds, setThresholds] = useState({ critical: 20, risk: 40, growing: 60, mature: 80 });
  const [branding, setBranding] = useState({ primary: "#064E3B", accent: "#D4AF37" });
  const [notifications, setNotifications] = useState({ weeklyDigest: true, pdfAlerts: true, newAdminAlerts: false });

  function handleSave() {
    setSaved(true);
    setTimeout(() => setSaved(false), 2500);
  }

  return (
    <>
      <AdminTopbar title="Settings" description="Root-only: global configuration for the whole system" />
      <div className="space-y-6 p-8">
        <Card>
          <CardHeader>
            <CardTitle>Maturity Score Thresholds</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="mb-4 text-sm text-graphite-500">
              Upper bound (out of 100) for each maturity band used across the console and PDF reports.
            </p>
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
              <div>
                <Label htmlFor="th-critical">Critical ≤</Label>
                <Input
                  id="th-critical"
                  type="number"
                  value={thresholds.critical}
                  onChange={(e) => setThresholds((t) => ({ ...t, critical: Number(e.target.value) }))}
                />
              </div>
              <div>
                <Label htmlFor="th-risk">Operational Risk ≤</Label>
                <Input
                  id="th-risk"
                  type="number"
                  value={thresholds.risk}
                  onChange={(e) => setThresholds((t) => ({ ...t, risk: Number(e.target.value) }))}
                />
              </div>
              <div>
                <Label htmlFor="th-growing">Growing ≤</Label>
                <Input
                  id="th-growing"
                  type="number"
                  value={thresholds.growing}
                  onChange={(e) => setThresholds((t) => ({ ...t, growing: Number(e.target.value) }))}
                />
              </div>
              <div>
                <Label htmlFor="th-mature">Mature ≤</Label>
                <Input
                  id="th-mature"
                  type="number"
                  value={thresholds.mature}
                  onChange={(e) => setThresholds((t) => ({ ...t, mature: Number(e.target.value) }))}
                />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Branding</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div>
                <Label htmlFor="brand-primary">Primary Color</Label>
                <div className="flex items-center gap-2">
                  <input
                    type="color"
                    value={branding.primary}
                    onChange={(e) => setBranding((b) => ({ ...b, primary: e.target.value }))}
                    className="h-9 w-9 cursor-pointer rounded border border-graphite-200"
                  />
                  <Input
                    value={branding.primary}
                    onChange={(e) => setBranding((b) => ({ ...b, primary: e.target.value }))}
                  />
                </div>
              </div>
              <div>
                <Label htmlFor="brand-accent">Accent Color</Label>
                <div className="flex items-center gap-2">
                  <input
                    type="color"
                    value={branding.accent}
                    onChange={(e) => setBranding((b) => ({ ...b, accent: e.target.value }))}
                    className="h-9 w-9 cursor-pointer rounded border border-graphite-200"
                  />
                  <Input
                    value={branding.accent}
                    onChange={(e) => setBranding((b) => ({ ...b, accent: e.target.value }))}
                  />
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Notifications</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <ToggleRow
              label="Weekly performance digest to all Admins"
              checked={notifications.weeklyDigest}
              onChange={(v) => setNotifications((n) => ({ ...n, weeklyDigest: v }))}
            />
            <ToggleRow
              label="Alert Root Admin when a PDF report is generated"
              checked={notifications.pdfAlerts}
              onChange={(v) => setNotifications((n) => ({ ...n, pdfAlerts: v }))}
            />
            <ToggleRow
              label="Alert Root Admin when a new Admin is invited"
              checked={notifications.newAdminAlerts}
              onChange={(v) => setNotifications((n) => ({ ...n, newAdminAlerts: v }))}
            />
          </CardContent>
        </Card>

        <div className="flex items-center gap-3">
          <Button onClick={handleSave}>Save Settings</Button>
          {saved && <span className="text-xs font-medium text-forest-700">Saved (demo only — not yet persisted).</span>}
        </div>
      </div>
    </>
  );
}

function ToggleRow({
  label,
  checked,
  onChange,
}: {
  label: string;
  checked: boolean;
  onChange: (value: boolean) => void;
}) {
  return (
    <label className="flex cursor-pointer items-center justify-between rounded-md border border-graphite-100 px-4 py-3">
      <span className="text-sm text-graphite-700">{label}</span>
      <span
        role="switch"
        aria-checked={checked}
        onClick={() => onChange(!checked)}
        className={`relative h-5 w-9 rounded-full transition-colors ${checked ? "bg-forest-700" : "bg-graphite-200"}`}
      >
        <span
          className={`absolute top-0.5 h-4 w-4 rounded-full bg-white transition-transform ${
            checked ? "translate-x-[18px]" : "translate-x-0.5"
          }`}
        />
      </span>
    </label>
  );
}
