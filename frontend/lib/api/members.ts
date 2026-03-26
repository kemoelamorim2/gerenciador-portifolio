import type { MemberRequest, MemberResponse } from "@/types/member";
import { apiClient } from "./client";
import { API_ROUTES } from "./config";

export async function createMember(payload: MemberRequest) {
  const response = await apiClient.post<MemberResponse>(API_ROUTES.members, payload);
  return response.data;
}

export async function getMembers() {
  const response = await apiClient.get<MemberResponse[]>(API_ROUTES.members);
  return response.data;
}

export async function getMemberById(memberId: number) {
  const response = await apiClient.get<MemberResponse>(`${API_ROUTES.members}/${memberId}`);
  return response.data;
}
