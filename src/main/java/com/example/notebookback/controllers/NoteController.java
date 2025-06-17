package com.example.notebookback.controllers;

import com.example.notebookback.models.DTOs.NotesDTO;
import com.example.notebookback.models.ntities.Note;
import com.example.notebookback.services.NoteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/notes")

public class NoteController {

    private static final Logger logger = Logger.getLogger(NoteController.class.getName());

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<NotesDTO> getNotes(
            @CookieValue(value = "jwt", required = false) String jwtToken,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        logger.info("jwtToken: " + jwtToken);
        logger.info("Полученный userId из заголовка: " + userId);
        NotesDTO notesDTO = noteService.searchNotes(title, startDate, endDate, page, size);
        return ResponseEntity.ok(notesDTO);
    }


    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        note.setId(null);
        Note savedNote = noteService.saveNote(note);
        return ResponseEntity.ok(savedNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note note) {
        note.setId(id);
        Note updatedNote = noteService.updateNote(note);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}

