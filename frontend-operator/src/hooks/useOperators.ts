import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchOperators, createOperator, type AvatarVariant } from "@/services/api";

export function useOperators() {
  return useQuery({ queryKey: ["operators"], queryFn: fetchOperators });
}

export function useCreateOperator() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: {
      name: string;
      lastName?: string;
      email: string;
      companyId: string;
      avatar?: AvatarVariant;
      status?: "Active" | "Invited";
    }) => createOperator(input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["operators"] });
      queryClient.invalidateQueries({ queryKey: ["companies"] });
    },
  });
}
