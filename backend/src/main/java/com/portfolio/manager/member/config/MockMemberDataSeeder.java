package com.portfolio.manager.member.config;

import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MockMemberDataSeeder implements CommandLineRunner {

    private final MemberRepository memberRepository;

    public MockMemberDataSeeder(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) {
        if (memberRepository.count() > 0) {
            return;
        }

        List<Member> members = List.of(
            createMember("Ana Martins", "funcionario"),
            createMember("Carlos Lima", "funcionario"),
            createMember("Juliana Rocha", "funcionario"),
            createMember("Pedro Henrique", "funcionario"),
            createMember("Larissa Gomes", "funcionario"),
            createMember("Rafael Costa", "funcionario"),
            createMember("Marina Alves", "gerente"),
            createMember("Bruno Ferreira", "analista"),
            createMember("Camila Nunes", "coordenador"),
            createMember("Felipe Barros", "designer"),
            createMember("Patricia Melo", "qa"),
            createMember("Thiago Ribeiro", "tech lead")
        );

        memberRepository.saveAll(members);
    }

    private Member createMember(String name, String assignment) {
        Member member = new Member();
        member.setName(name);
        member.setAssignment(assignment);
        return member;
    }
}
