package com.efacility.ticketing.repository.spec;

import com.efacility.ticketing.model.Tiket;
import com.efacility.ticketing.model.enums.Prioritet;
import com.efacility.ticketing.model.enums.StatusTiketa;
import org.springframework.data.jpa.domain.Specification;

/**
 * Dinamicko filtriranje/pretraga liste tiketa (status, prioritet, zgrada, kljucna rec u
 * naslovu/opisu). Koristi se u TiketService.getAllTikets(...) preko JpaSpecificationExecutor,
 * u kombinaciji sa Pageable za paginaciju i sortiranje (GET /tickets/all).
 */
public class TiketSpecifications {

    private TiketSpecifications() {
    }

    public static Specification<Tiket> withFilters(StatusTiketa status, Prioritet priority,
                                                     Long buildingId, String search) {
        return Specification
                .where(hasStatus(status))
                .and(hasPriority(priority))
                .and(inBuilding(buildingId))
                .and(matchesSearch(search));
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
