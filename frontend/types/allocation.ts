export type ProjectMemberAllocationRequest = {
  memberId: number;
};

export type ProjectMemberAllocationResponse = {
  allocationId: number;
  projectId: number;
  memberId: number;
  memberName: string;
  memberAssignment: string;
};
