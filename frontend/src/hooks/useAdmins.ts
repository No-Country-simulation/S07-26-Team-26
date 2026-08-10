import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchAdmins, createAdmin, type AdminRole, type AvatarVariant } from "@/services/api";

export function useAdmins() {
  return useQuery({ queryKey: ["admins"], queryFn: fetchAdmins });
}

export function useCreateAdmin() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: {
      name: string;
      lastName?: string;
      email: string;
      role: AdminRole;
      avatar?: AvatarVariant;
      status?: "Active" | "Invited";
    }) => createAdmin(input),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admins"] }),
  });
}
