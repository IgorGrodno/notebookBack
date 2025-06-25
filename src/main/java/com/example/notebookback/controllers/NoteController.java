package com.example.notebookback.controllers;

import com.example.notebookback.models.DTOs.NoteDTO;
import com.example.notebookback.models.DTOs.NotesDTO;
import com.example.notebookback.models.ntities.Note;
import com.example.notebookback.repositories.UserRepository;
import com.example.notebookback.services.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    private final NoteService noteService;
    private final UserRepository userRepository;

    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<NotesDTO> getNotes(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        try {
            Long uid = Long.parseLong(userId);
            NotesDTO notesDTO = noteService.searchNotes(uid, title, startDate, endDate, page, size);
            logger.info("Fetched notes for userId {}: {}", uid, notesDTO);
            return ResponseEntity.ok(notesDTO);
        } catch (NumberFormatException e) {
            logger.warn("Invalid userId format: {}", userId);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error fetching notes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Note> createNote(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Note note
    ) {
        try {
            Long uid = Long.parseLong(userId);
            note.setId(null);
            note.setUser(userRepository.findById(uid)
                    .orElseThrow(() -> new NoSuchElementException("User not found with id: " + uid)));
            Note savedNote = noteService.saveNote(note);
            logger.info("Created note with id: {}", savedNote.getId());
            return ResponseEntity.ok(savedNote);
        } catch (NumberFormatException e) {
            logger.warn("Invalid userId format: {}", userId);
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error creating note", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id, @RequestBody Note note) {
        try {
            note.setId(id);
            NoteDTO updatedNote = noteService.updateNote(note);
            logger.info("Updated note with id: {}", updatedNote.getId());
            return ResponseEntity.ok(updatedNote);
        } catch (NoSuchElementException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating note", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        try {
            noteService.deleteNote(id);
            logger.info("Deleted note with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting note", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
