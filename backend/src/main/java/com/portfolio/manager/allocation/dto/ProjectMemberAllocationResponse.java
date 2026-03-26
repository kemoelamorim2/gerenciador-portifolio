package com.portfolio.manager.allocation.dto;

public record ProjectMemberAllocationResponse(
    Long allocationId,
    Long projectId,
    Long memberId,
    String memberName,
    String memberAssignment
) {
}
