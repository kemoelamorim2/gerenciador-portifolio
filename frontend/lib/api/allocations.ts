import type {
  ProjectMemberAllocationRequest,
  ProjectMemberAllocationResponse,
} from "@/types/allocation";
import { apiClient } from "./client";
import { API_ROUTES } from "./config";

export async function allocateProjectMember(
  projectId: number,
  payload: ProjectMemberAllocationRequest,
) {
  const response = await apiClient.post<ProjectMemberAllocationResponse>(
    `${API_ROUTES.projects}/${projectId}/members`,
    payload,
  );
  return response.data;
}

export async function getProjectMembers(projectId: number) {
  const response = await apiClient.get<ProjectMemberAllocationResponse[]>(
    `${API_ROUTES.projects}/${projectId}/members`,
  );
  return response.data;
}

export async function removeProjectMember(projectId: number, memberId: number) {
  await apiClient.delete(`${API_ROUTES.projects}/${projectId}/members/${memberId}`);
}
