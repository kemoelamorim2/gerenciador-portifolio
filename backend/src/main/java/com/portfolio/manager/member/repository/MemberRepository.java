package com.portfolio.manager.member.repository;

import com.portfolio.manager.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
