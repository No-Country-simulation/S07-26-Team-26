"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useRouter } from "next/navigation";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Button } from "@/components/ui/Button";
import { useAuthStore } from "@/store/authStore";

const schema = z.object({
  companyName: z.string().min(2, "Required"),
  corporateEmail: z.string().email(),
  country: z.string().min(2, "Required"),
  industry: z.string().min(2, "Required"),
  employees: z.string().min(1, "Required"),
  dataCenterTier: z.string().min(1, "Required"),
  gpuClusterSize: z.coerce.number().min(1, "Must be greater than 0"),
});

type FormValues = z.infer<typeof schema>;

export function ProfileForm() {
  const router = useRouter();
  const session = useAuthStore((s) => s.session);
  const setProfile = useAuthStore((s) => s.setProfile);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      companyName: session?.organization ?? "",
      corporateEmail: session?.email ?? "",
      country: "",
      industry: "",
      employees: "",
      dataCenterTier: "",
      gpuClusterSize: undefined,
    },
  });

  async function onSubmit(values: FormValues) {
    setProfile(values);
    router.push("/operator/calculator");
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="grid grid-cols-1 gap-5 sm:grid-cols-2">
      <div>
        <Label htmlFor="companyName">Company Name</Label>
        <Input id="companyName" {...register("companyName")} />
        {errors.companyName && <p className="mt-1 text-xs text-red-600">{errors.companyName.message}</p>}
      </div>

      <div>
        <Label htmlFor="corporateEmail">Corporate Email</Label>
        <Input id="corporateEmail" type="email" {...register("corporateEmail")} />
        {errors.corporateEmail && <p className="mt-1 text-xs text-red-600">{errors.corporateEmail.message}</p>}
      </div>

      <div>
        <Label htmlFor="country">Country</Label>
        <Input id="country" placeholder="United States" {...register("country")} />
        {errors.country && <p className="mt-1 text-xs text-red-600">{errors.country.message}</p>}
      </div>

      <div>
        <Label htmlFor="industry">Industry</Label>
        <Input id="industry" placeholder="Cloud Compute" {...register("industry")} />
        {errors.industry && <p className="mt-1 text-xs text-red-600">{errors.industry.message}</p>}
      </div>

      <div>
        <Label htmlFor="employees">Employees</Label>
        <select
          id="employees"
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
        <Label htmlFor="dataCenterTier">Data Center Tier</Label>
        <select
          id="dataCenterTier"
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

      <div>
        <Label htmlFor="gpuClusterSize">GPU Cluster Size</Label>
        <Input id="gpuClusterSize" type="number" placeholder="4200" {...register("gpuClusterSize")} />
        {errors.gpuClusterSize && <p className="mt-1 text-xs text-red-600">{errors.gpuClusterSize.message}</p>}
      </div>

      <div className="sm:col-span-2">
        <Button type="submit" className="w-full sm:w-auto" loading={isSubmitting}>
          Continue to Calculator
        </Button>
      </div>
    </form>
  );
}
