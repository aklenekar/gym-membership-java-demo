package com.apexgym.controller;

import com.apexgym.entity.FitnessClass;
import com.apexgym.service.FitnessClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class FitnessClassController {

    private final FitnessClassService service;

    /** -------------------------------------------------------------
     *  GET  /api/classes
     *  Optional query params: category, instructor, day
     * ------------------------------------------------------------- */
    @GetMapping
    public ResponseEntity<List<FitnessClass>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String instructor,
            @RequestParam(required = false) String day) {

        List<FitnessClass> classes = service.findByFilters(category, instructor, day);
        return ResponseEntity.ok(classes);
    }

    /** -------------------------------------------------------------
     *  GET  /api/classes/{id}
     * ------------------------------------------------------------- */
    @GetMapping("/{id}")
    public ResponseEntity<FitnessClass> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /** -------------------------------------------------------------
     *  POST /api/classes/{id}/book   → book a spot
     * ------------------------------------------------------------- */
    @PostMapping("/{id}/book")
    public ResponseEntity<FitnessClass> book(@PathVariable Long id) {
        FitnessClass booked = service.bookClass(id);
        return ResponseEntity.ok(booked);
    }

    /** -------------------------------------------------------------
     *  DELETE /api/classes/{id}/book   → cancel a spot
     * ------------------------------------------------------------- */
    @DeleteMapping("/{id}/book")
    public ResponseEntity<FitnessClass> cancel(@PathVariable Long id) {
        FitnessClass cancelled = service.cancelBooking(id);
        return ResponseEntity.ok(cancelled);
    }
}
