import { useQuery } from "@tanstack/react-query";
import { fetchCampaigns } from "@/services/api";

export function useCampaigns() {
  return useQuery({ queryKey: ["campaigns"], queryFn: fetchCampaigns });
}
