package com.open.crm.core.application.specification;

import com.open.crm.core.entities.employee.Employee;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

public final class EmployeeSpecification {
	private EmployeeSpecification() {}

    public static Specification<Employee> findByPhoneNumber(String phoneNumber) {
        return (root, query, cb) -> cb.or(
            cb.like(root.get("phoneNumber"), phoneNumber + "%"),
            cb.like(root.get("phoneNumber"), "% " + phoneNumber)
        );
    }

    public static Specification<Employee> findByPosition(String position) {
        return (root, query, cb) -> cb.equal(root.get("position"), position);
    }

    public static Specification<Employee> findByEmail(String email) {
        return (root, query, cb) -> cb.like(root.get("email"), "%" + email + "%");
    }

    public static Specification<Employee> findByFullname(String fullname) {
        return (root, query, cb) -> {
            if (Objects.isNull(fullname) || fullname.isBlank()) {
                return cb.conjunction();
            }

            double similarityThreshold = 0.3;
            String normalizedQuery = fullname.trim().toLowerCase(Locale.ROOT);

            if (normalizedQuery.length() <= 4) similarityThreshold = 0.2;
            Expression<String> concatName = cb.concat(root.get("lastname"), cb.literal(" "));
            concatName = cb.concat(cb.concat(concatName, root.get("firstname")), cb.literal(" "));
            concatName = cb.concat(concatName, root.get("patronymic"));

            return cb.or(
                cb.like(cb.lower(concatName), "%" + normalizedQuery + "%"),
                cb.greaterThanOrEqualTo(
                    cb.function(
                        "similarity",
                        Double.class,
                        cb.lower(concatName),
                        cb.literal(normalizedQuery)
                    ),
                    similarityThreshold
                )
            );
        };
    }
}
