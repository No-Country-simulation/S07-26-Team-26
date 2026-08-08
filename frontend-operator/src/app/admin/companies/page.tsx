"use client";

import { useState } from "react";
import { AdminTopbar } from "@/components/navbar/AdminTopbar";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Skeleton";
import { CompaniesTable } from "@/components/dashboard/CompaniesTable";
import { RegisterCompanyForm } from "@/components/forms/RegisterCompanyForm";
import { CompanyCsvUploadCard } from "@/components/forms/CompanyCsvUploadCard";
import { useVisibleCompanies } from "@/hooks/useCompanies";
import { useAuthStore } from "@/store/authStore";
import { PixelBuilding } from "@/components/blocks/PixelIcons";

export default function AdminCompaniesPage() {
  const { data: companies, isLoading } = useVisibleCompanies();
  const [showRegister, setShowRegister] = useState(false);
  const session = useAuthStore((s) => s.session);
  const isRoot = session?.role === "ROOT_ADMIN";

  return (
    <>
      <AdminTopbar
        title="Companies"
        description={
          isRoot
            ? "Every organization registered across the system"
            : "Organizations assigned to you"
        }
      />
      <div className="space-y-6 p-8">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <PixelBuilding size={20} />
              Register Companies
            </CardTitle>
            <Button variant="secondary" size="sm" onClick={() => setShowRegister((v) => !v)}>
              {showRegister ? "Hide" : "Register a company"}
            </Button>
          </CardHeader>
          {showRegister && (
            <CardContent className="grid grid-cols-1 gap-8 lg:grid-cols-2">
              <div>
                <p className="mb-1 flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-gold-700">
                  <span className="h-px w-4 bg-gold-500" />
                  Manual
                </p>
                <h3 className="mb-4 text-base font-semibold text-graphite-900">Register a single company</h3>
                <RegisterCompanyForm />
              </div>
              <div className="border-t border-graphite-100 pt-6 lg:border-l lg:border-t-0 lg:pl-8 lg:pt-0">
                <CompanyCsvUploadCard />
              </div>
            </CardContent>
          )}
        </Card>

        <Card>
          <CardContent className="pt-5">
            {isLoading || !companies ? (
              <Skeleton className="h-64" />
            ) : (
              <CompaniesTable companies={companies} />
            )}
          </CardContent>
        </Card>
      </div>
    </>
  );
}
