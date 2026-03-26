package com.portfolio.manager.project.specification;

import com.portfolio.manager.project.dto.ProjectFilterRequest;
import com.portfolio.manager.project.entity.Project;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<Project> withFilters(ProjectFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.upper(root.get("name")),
                    "%" + filter.getName().trim().toUpperCase() + "%"
                ));
            }

            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getRiskLevel() != null) {
                predicates.add(criteriaBuilder.equal(root.get("riskLevel"), filter.getRiskLevel()));
            }

            if (filter.getManagerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("manager").get("id"), filter.getManagerId()));
            }

            if (filter.getBudgetMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("budget"), filter.getBudgetMin()));
            }

            if (filter.getBudgetMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("budget"), filter.getBudgetMax()));
            }

            if (filter.getStartDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDateFrom()));
            }

            if (filter.getStartDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), filter.getStartDateTo()));
            }

            if (filter.getExpectedEndDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("expectedEndDate"),
                    filter.getExpectedEndDateFrom()
                ));
            }

            if (filter.getExpectedEndDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("expectedEndDate"),
                    filter.getExpectedEndDateTo()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
