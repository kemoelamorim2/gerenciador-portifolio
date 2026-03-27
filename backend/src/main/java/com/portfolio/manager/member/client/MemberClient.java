package com.portfolio.manager.member.client;

import com.portfolio.manager.member.entity.Member;
import java.util.List;

public interface MemberClient {

    Member findById(Long id);

    List<Member> findAll();
}
