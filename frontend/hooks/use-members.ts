"use client";

import { createMember, getMemberById, getMembers } from "@/lib/api/members";

export function useMembers() {
  return {
    createMember,
    getMembers,
    getMemberById,
  };
}
