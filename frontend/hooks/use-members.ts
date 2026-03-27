"use client";

import { getMemberById, getMembers } from "@/lib/api/members";

export function useMembers() {
  return {
    getMembers,
    getMemberById,
  };
}
