package com.apexgym.equipment.persistence.specification;

import com.apexgym.equipment.entity.Equipment;
import com.apexgym.equipment.entity.enums.EquipmentCategory;
import com.apexgym.equipment.entity.enums.EquipmentStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EquipmentSpecifications {

    public static Specification<Equipment> withFilters(
            EquipmentCategory category,
            EquipmentStatus status,
            String location,
            String search) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (StringUtils.hasText(location)) {
                predicates.add(cb.equal(root.get("location"), location));
            }

            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate serialMatch = cb.like(cb.lower(root.get("serialNumber")), searchPattern);
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), searchPattern);
                predicates.add(cb.or(nameMatch, serialMatch, brandMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}