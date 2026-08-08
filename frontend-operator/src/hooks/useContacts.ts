import { useMutation } from "@tanstack/react-query";
import { prepareCampaignFromContacts } from "@/services/api";

export function usePrepareCampaign() {
  return useMutation({ mutationFn: prepareCampaignFromContacts });
}
