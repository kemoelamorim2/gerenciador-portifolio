import type { PagedResponse } from "@/types/api";
import type {
  ProjectCreateRequest,
  ProjectFilterRequest,
  ProjectResponse,
  ProjectStatusUpdateRequest,
  ProjectUpdateRequest,
} from "@/types/project";
import { apiClient } from "./client";
import { API_ROUTES } from "./config";

export async function createProject(payload: ProjectCreateRequest) {
  const response = await apiClient.post<ProjectResponse>(API_ROUTES.projects, payload);
  return response.data;
}

export async function getProjects(params: ProjectFilterRequest = {}) {
  const response = await apiClient.get<PagedResponse<ProjectResponse>>(API_ROUTES.projects, {
    params,
  });
  return response.data;
}

export async function getProjectById(projectId: number) {
  const response = await apiClient.get<ProjectResponse>(`${API_ROUTES.projects}/${projectId}`);
  return response.data;
}

export async function updateProject(projectId: number, payload: ProjectUpdateRequest) {
  const response = await apiClient.put<ProjectResponse>(
    `${API_ROUTES.projects}/${projectId}`,
    payload,
  );
  return response.data;
}

export async function updateProjectStatus(
  projectId: number,
  payload: ProjectStatusUpdateRequest,
) {
  const response = await apiClient.patch<ProjectResponse>(
    `${API_ROUTES.projects}/${projectId}/status`,
    payload,
  );
  return response.data;
}

export async function deleteProject(projectId: number) {
  await apiClient.delete(`${API_ROUTES.projects}/${projectId}`);
}
