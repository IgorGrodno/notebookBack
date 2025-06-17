package com.example.notebookback.services;

import com.example.notebookback.models.DTOs.NotesDTO;
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
import java.util.NoSuchElementException;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public NotesDTO searchNotes(Long userId, String title, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Specification<Note> spec = buildSpecification(userId, title, startDate, endDate);

        Page<Note> notesPage = noteRepository.findAll(spec, pageable);

        NotesDTO dto = new NotesDTO();
        dto.setNotes(notesPage.getContent());
        dto.setTotalNotes(notesPage.getTotalElements());
        dto.setPageSize(notesPage.getSize());
        dto.setPageIndex(notesPage.getNumber());
        dto.setTotalPages(notesPage.getTotalPages());

        return dto;
    }

    private Specification<Note> buildSpecification(Long userId, String title, LocalDateTime startDate,
                                                   LocalDateTime endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));
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
    }

    public Note saveNote(Note note) {
        return noteRepository.save(note);
    }

    public Note updateNote(Note note) {
        if (!noteRepository.existsById(note.getId())) {
            throw new NoSuchElementException("Note not found with id: " + note.getId());
        }
        return noteRepository.save(note);
    }

    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new NoSuchElementException("Note not found with id: " + id);
        }
        noteRepository.deleteById(id);
    }
}

