package com.apexgym.personaltraining.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionNoteRepository extends JpaRepository<SessionNote, Long> {
    List<SessionNote> findBySessionIdOrderByCreatedAtDesc(Long sessionId);
    List<SessionNote> findBySession_User_IdOrderByCreatedAtDesc(Long userId);
}