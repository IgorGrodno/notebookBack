package com.example.notebookback.services;

import com.example.notebookback.models.ntities.Note;
import com.example.notebookback.repositories.NoteRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Page<Note> searchNotes(String title, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Specification<Note> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return noteRepository.findAll(spec, pageable);
    }

    public Note saveNote(Note note) {
        return noteRepository.save(note);
    }
}

