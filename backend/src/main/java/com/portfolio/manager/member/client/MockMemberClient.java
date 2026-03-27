package com.portfolio.manager.member.client;

import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MockMemberClient implements MemberClient {

    private final MemberRepository memberRepository;

    public MockMemberClient(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member findById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }
}
