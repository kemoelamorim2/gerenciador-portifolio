package com.portfolio.manager.project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void shouldPersistProjectWithManager() {
        Member manager = new Member();
        manager.setName("Carla");
        manager.setAssignment("gerente");
        Member savedManager = memberRepository.save(manager);

        Project project = new Project();
        project.setName("Projeto Repositorio");
        project.setStartDate(LocalDate.of(2026, 3, 26));
        project.setExpectedEndDate(LocalDate.of(2026, 9, 26));
        project.setBudget(new BigDecimal("300000.00"));
        project.setDescription("Projeto salvo em teste JPA");
        project.setManager(savedManager);
        project.setStatus(ProjectStatus.EM_ANALISE);
        project.setRiskLevel(RiskLevel.MEDIO);

        Project savedProject = projectRepository.save(project);

        assertTrue(projectRepository.existsByNameIgnoreCase("projeto repositorio"));
        assertEquals(savedManager.getId(), savedProject.getManager().getId());
    }
}
