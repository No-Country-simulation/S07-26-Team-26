"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Button } from "@/components/ui/Button";
import { useCreateCompany } from "@/hooks/useCompanies";
import { useAuthStore } from "@/store/authStore";

const schema = z.object({
  name: z.string().min(2, "Required"),
  email: z.string().email("Enter a valid email"),
  country: z.string().min(2, "Required"),
  industry: z.string().min(2, "Required"),
  employees: z.string().min(1, "Required"),
  dataCenterTier: z.string().min(1, "Required"),
  gpuClusterSize: z.coerce.number().min(1, "Must be greater than 0"),
});

type FormValues = z.infer<typeof schema>;

export function RegisterCompanyForm() {
  const session = useAuthStore((s) => s.session);
  const createCompany = useCreateCompany();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  async function onSubmit(values: FormValues) {
    await createCompany.mutateAsync({
      ...values,
      // A scoped Admin's new companies land in their own scope automatically.
      // Root Admin creates unassigned companies here; assign an owner from
      // the Admins page.
      assignedAdminId: session?.role === "ADMIN" ? session.adminId ?? null : null,
    });
    reset();
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="grid grid-cols-1 gap-5 sm:grid-cols-2">
      <div>
        <Label htmlFor="company-name">Company Name</Label>
        <Input id="company-name" {...register("name")} />
        {errors.name && <p className="mt-1 text-xs text-red-600">{errors.name.message}</p>}
      </div>
      <div>
        <Label htmlFor="company-email">Primary Contact Email</Label>
        <Input id="company-email" type="email" {...register("email")} />
        {errors.email && <p className="mt-1 text-xs text-red-600">{errors.email.message}</p>}
      </div>
      <div>
        <Label htmlFor="company-country">Country</Label>
        <Input id="company-country" placeholder="United States" {...register("country")} />
        {errors.country && <p className="mt-1 text-xs text-red-600">{errors.country.message}</p>}
      </div>
      <div>
        <Label htmlFor="company-industry">Industry</Label>
        <Input id="company-industry" placeholder="Cloud Compute" {...register("industry")} />
        {errors.industry && <p className="mt-1 text-xs text-red-600">{errors.industry.message}</p>}
      </div>
      <div>
        <Label htmlFor="company-employees">Employees</Label>
        <select
          id="company-employees"
          className="w-full rounded-md border border-graphite-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-forest-700/40"
          {...register("employees")}
        >
          <option value="">Select a range</option>
          <option>1-100</option>
          <option>100-500</option>
          <option>500-1,000</option>
          <option>1,000-5,000</option>
          <option>5,000+</option>
        </select>
        {errors.employees && <p className="mt-1 text-xs text-red-600">{errors.employees.message}</p>}
      </div>
      <div>
        <Label htmlFor="company-tier">Data Center Tier</Label>
        <select
          id="company-tier"
          className="w-full rounded-md border border-graphite-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-forest-700/40"
          {...register("dataCenterTier")}
        >
          <option value="">Select a tier</option>
          <option>Tier I</option>
          <option>Tier II</option>
          <option>Tier III</option>
          <option>Tier IV</option>
        </select>
        {errors.dataCenterTier && <p className="mt-1 text-xs text-red-600">{errors.dataCenterTier.message}</p>}
      </div>
      <div className="sm:col-span-2">
        <Label htmlFor="company-gpu">GPU Cluster Size</Label>
        <Input id="company-gpu" type="number" placeholder="4200" {...register("gpuClusterSize")} />
        {errors.gpuClusterSize && <p className="mt-1 text-xs text-red-600">{errors.gpuClusterSize.message}</p>}
      </div>
      <div className="sm:col-span-2">
        <Button type="submit" loading={createCompany.isPending}>
          Register Company
        </Button>
      </div>
    </form>
  );
}
