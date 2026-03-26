"use client";

import {
  createProject,
  deleteProject,
  getProjectById,
  getProjects,
  updateProject,
  updateProjectStatus,
} from "@/lib/api/projects";

export function useProjects() {
  return {
    createProject,
    getProjects,
    getProjectById,
    updateProject,
    updateProjectStatus,
    deleteProject,
  };
}
