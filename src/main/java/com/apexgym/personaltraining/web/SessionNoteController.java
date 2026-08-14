package com.apexgym.personaltraining.web;

import com.apexgym.personaltraining.dto.CreateSessionNoteRequest;
import com.apexgym.personaltraining.dto.SessionNoteDTO;
import com.apexgym.personaltraining.service.SessionNoteService;
import com.apexgym.shared.CommonHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personal-training/sessions/{sessionId}/notes")
@RequiredArgsConstructor
public class SessionNoteController {

    private final SessionNoteService noteService;
    private final CommonHelper commonHelper;

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<SessionNoteDTO> addNote(
            @PathVariable Long sessionId,
            @Valid @RequestBody CreateSessionNoteRequest request) {
        String email = commonHelper.getCurrentUserEmail();
        return ResponseEntity.ok(noteService.addNote(email, sessionId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TRAINER','USER')")
    public ResponseEntity<List<SessionNoteDTO>> getNotes(@PathVariable Long sessionId) {
        return ResponseEntity.ok(noteService.getNotesForSession(sessionId));
    }
}