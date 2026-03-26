package com.portfolio.manager.allocation.repository;

import com.portfolio.manager.allocation.entity.ProjectMemberAllocation;
import com.portfolio.manager.project.enums.ProjectStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectMemberAllocationRepository extends JpaRepository<ProjectMemberAllocation, Long> {

    long countByProjectId(Long projectId);

    boolean existsByProjectIdAndMemberId(Long projectId, Long memberId);

    Optional<ProjectMemberAllocation> findByProjectIdAndMemberId(Long projectId, Long memberId);

    List<ProjectMemberAllocation> findAllByProjectId(Long projectId);

    @Query("""
        select count(distinct a.project.id)
        from ProjectMemberAllocation a
        where a.member.id = :memberId
          and a.project.status not in :inactiveStatuses
    """)
    long countDistinctActiveProjectsByMemberId(
        @Param("memberId") Long memberId,
        @Param("inactiveStatuses") Collection<ProjectStatus> inactiveStatuses
    );

    @Query("select count(distinct a.member.id) from ProjectMemberAllocation a")
    long countDistinctAllocatedMembers();
}
