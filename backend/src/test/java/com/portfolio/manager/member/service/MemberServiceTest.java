package com.portfolio.manager.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.member.dto.MemberRequest;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository);
    }

    @Test
    void shouldCreateMember() {
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setId(1L);
            return member;
        });

        var response = memberService.create(new MemberRequest("Ana", "funcionario"));

        assertEquals(1L, response.id());
        assertEquals("Ana", response.name());
    }

    @Test
    void shouldListMembers() {
        when(memberRepository.findAll()).thenReturn(List.of(new Member(1L, "Ana", "funcionario")));

        var response = memberService.findAll();

        assertEquals(1, response.size());
    }

    @Test
    void shouldThrowWhenMemberNotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.findById(1L));
    }
}
