"use client";

import {
  allocateProjectMember,
  getProjectMembers,
  removeProjectMember,
} from "@/lib/api/allocations";

export function useProjectMembers() {
  return {
    allocateProjectMember,
    getProjectMembers,
    removeProjectMember,
  };
}
