package com.portfolio.manager.project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import com.portfolio.manager.project.dto.ProjectFilterRequest;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import com.portfolio.manager.project.specification.ProjectSpecification;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

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

    @Test
    void shouldFilterProjectsByStatusAndManager() {
        Member managerOne = new Member();
        managerOne.setName("Carla");
        managerOne.setAssignment("gerente");
        managerOne = memberRepository.save(managerOne);

        Member managerTwo = new Member();
        managerTwo.setName("Pedro");
        managerTwo.setAssignment("gerente");
        managerTwo = memberRepository.save(managerTwo);

        projectRepository.save(buildProject("Projeto A", managerOne, ProjectStatus.EM_ANALISE, RiskLevel.BAIXO));
        projectRepository.save(buildProject("Projeto B", managerTwo, ProjectStatus.EM_ANDAMENTO, RiskLevel.ALTO));

        ProjectFilterRequest filter = new ProjectFilterRequest();
        filter.setStatus(ProjectStatus.EM_ANALISE);
        filter.setManagerId(managerOne.getId());

        var page = projectRepository.findAll(ProjectSpecification.withFilters(filter), PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Projeto A", page.getContent().get(0).getName());
    }

    private Project buildProject(String name, Member manager, ProjectStatus status, RiskLevel riskLevel) {
        Project project = new Project();
        project.setName(name);
        project.setStartDate(LocalDate.of(2026, 3, 26));
        project.setExpectedEndDate(LocalDate.of(2026, 9, 26));
        project.setBudget(new BigDecimal("300000.00"));
        project.setDescription("Projeto salvo em teste JPA");
        project.setManager(manager);
        project.setStatus(status);
        project.setRiskLevel(riskLevel);
        return project;
    }
}
