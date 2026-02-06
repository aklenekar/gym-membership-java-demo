package com.apexgym.service;

import com.apexgym.entity.FitnessClass;
import com.apexgym.repository.FitnessClassRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FitnessClassService {

    private final FitnessClassRepository repo;

    /** -----------------------------------------------------------------
     *  READ
     * ----------------------------------------------------------------- */
    public List<FitnessClass> findAll() {
        return repo.findAll();
    }

    /**
     * Retrieve classes with optional filters.
     *
     * @param category   filter by category (e.g. "HIIT") – pass null/empty for no filter
     * @param instructor filter by instructor name – pass null/empty for no filter
     * @param day        "today", "tomorrow", "week" or null/empty
     */
    public List<FitnessClass> findByFilters(String category,
                                            String instructor,
                                            String day) {

        Specification<FitnessClass> spec = Specification.where(null);

        if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
        }

        if (instructor != null && !instructor.isBlank() && !"all".equalsIgnoreCase(instructor)) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("instructor")), instructor.toLowerCase()));
        }

        if (day != null && !day.isBlank() && !"all".equalsIgnoreCase(day)) {
            LocalDate start;
            LocalDate end = null;

            switch (day.toLowerCase()) {
                case "today" -> start = LocalDate.now();
                case "tomorrow" -> start = LocalDate.now().plusDays(1);
                case "week" -> {
                    start = LocalDate.now();
                    end = start.plusDays(6); // inclusive 7‑day window
                }
                default -> start = null;
            }

            if (start != null) {
                if (end == null) { // a single day
                    spec = spec.and((root, query, cb) ->
                            cb.between(cb.function("date", LocalDate.class, root.get("startTime")),
                                    start, start));
                } else { // range (week)
                    spec = spec.and((root, query, cb) ->
                            cb.between(cb.function("date", LocalDate.class, root.get("startTime")),
                                    start, LocalDate.now()));
                }
            }
        }

        return repo.findAll(spec);
    }

    public FitnessClass getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id " + id));
    }

    /** -----------------------------------------------------------------
     *  BOOKING LOGIC
     * ----------------------------------------------------------------- */
    @Transactional
    public FitnessClass bookClass(Long classId) {
        FitnessClass fc = repo.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id " + classId));

        if (!fc.hasFreeSpots()) {
            throw new IllegalStateException("No spots left for class '" + fc.getCategory() + "'");
        }

        fc.setBooked(fc.getBooked() + 1);
        return repo.save(fc);
    }

    @Transactional
    public FitnessClass cancelBooking(Long classId) {
        FitnessClass fc = repo.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id " + classId));

        if (fc.getBooked() <= 0) {
            throw new IllegalStateException("No bookings to cancel for class '" + fc.getCategory() + "'");
        }

        fc.setBooked(fc.getBooked() - 1);
        return repo.save(fc);
    }

}
