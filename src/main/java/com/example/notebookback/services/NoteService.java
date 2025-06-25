package com.example.notebookback.services;

import com.example.notebookback.models.DTOs.NoteDTO;
import com.example.notebookback.models.DTOs.NotesDTO;
import com.example.notebookback.models.ntities.Note;
import com.example.notebookback.repositories.NoteRepository;
import com.example.notebookback.utils.NoteMapper;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public NotesDTO searchNotes(Long userId, String title, LocalDateTime startDate, LocalDateTime endDate, int page,
                                int size) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Specification<Note> spec = buildSpecification(userId, title, startDate, endDate);

        Page<NoteDTO> notesPage =NoteMapper.toDto(noteRepository.findAll(spec, pageable));

        NotesDTO dto = new NotesDTO();
        dto.setNotes(notesPage.getContent());
        dto.setTotalNotes(notesPage.getTotalElements());
        dto.setPageSize(notesPage.getSize());
        dto.setPageIndex(notesPage.getNumber());
        dto.setTotalPages(notesPage.getTotalPages());

        return dto;
    }

    private Specification<Note> buildSpecification(Long userId, String title, LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            var userJoin = root.join("user");
            predicates.add(cb.equal(userJoin.get("id"), userId));

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
        Note savedNote = noteRepository.save(note);
        logger.info("Saved note with id: {}", savedNote.getId());
        return savedNote;
    }

    public NoteDTO updateNote(Note note) {
       Note updatedNote = noteRepository.getReferenceById(note.getId());
       updatedNote.setTitle(note.getTitle());
       updatedNote.setText(note.getText());
       noteRepository.save(updatedNote);
       logger.info("Updated note with id: {}", updatedNote.getId());
       return NoteMapper.toDto(updatedNote);
    }

    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Note not found with id: " + id));
        noteRepository.delete(note);
        logger.info("Deleted note with id: {}", id);
    }
}
