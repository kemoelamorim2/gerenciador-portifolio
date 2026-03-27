package com.portfolio.manager.member.service;

import com.portfolio.manager.member.dto.MemberRequest;
import com.portfolio.manager.member.dto.MemberResponse;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import com.portfolio.manager.exception.ResourceNotFoundException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse create(MemberRequest request) {
        Member member = new Member();
        member.setName(request.name());
        member.setAssignment(normalizeAssignment(request.assignment()));

        Member savedMember = memberRepository.save(member);
        return toResponse(savedMember);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse findById(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        return toResponse(member);
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getName(),
            member.getAssignment()
        );
    }

    private String normalizeAssignment(String assignment) {
        String normalized = Normalizer.normalize(assignment, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");

        return normalized.trim().toLowerCase(Locale.ROOT);
    }
}
