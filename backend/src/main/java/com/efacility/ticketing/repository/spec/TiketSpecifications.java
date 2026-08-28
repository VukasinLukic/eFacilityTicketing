package com.efacility.ticketing.repository.spec;

import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TiketSpecifications {

    private TiketSpecifications() {
    }

    public static Specification<Tiket> withFilters(StatusTiketa status, Prioritet priority,
                                                     Long buildingId, String search) {
        return withFilters(status, priority, buildingId, search, null, null, null);
    }

    public static Specification<Tiket> withFilters(StatusTiketa status, Prioritet priority,
                                                     Long buildingId, String search,
                                                     LocalDate from, LocalDate to,
                                                     Long technicianId) {
        return Specification
                .where(hasStatus(status))
                .and(hasPriority(priority))
                .and(inBuilding(buildingId))
                .and(matchesSearch(search))
                .and(createdBetween(from, to))
                .and(hasTechnician(technicianId));
    }

    public static Specification<Tiket> createdBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return null;
            }
            if (from != null && to != null) {
                return cb.between(root.get("createdAt"), from.atStartOfDay(), to.atTime(LocalTime.MAX));
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay());
            }
            return cb.lessThanOrEqualTo(root.<LocalDateTime>get("createdAt"), to.atTime(LocalTime.MAX));
        };
    }

    public static Specification<Tiket> hasTechnician(Long technicianId) {
        return (root, query, cb) -> technicianId == null ? null
                : cb.equal(root.get("technician").get("id"), technicianId);
    }

    private static Specification<Tiket> hasStatus(StatusTiketa status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Tiket> hasPriority(Prioritet priority) {
        return (root, query, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    private static Specification<Tiket> inBuilding(Long buildingId) {
        return (root, query, cb) -> buildingId == null ? null
                : cb.equal(root.get("apartment").get("building").get("id"), buildingId);
    }

    private static Specification<Tiket> matchesSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String likePattern = "%" + search.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }
}
