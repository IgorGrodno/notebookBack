package com.example.notebookback.controllers;

import com.example.notebookback.models.DTOs.NotesDTO;
import com.example.notebookback.models.ntities.Note;
import com.example.notebookback.repositories.UserRepository;
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
        try{
            NotesDTO notesDTO = noteService.searchNotes(Long.parseLong(userId), title, startDate, endDate, page, size);
            return ResponseEntity.ok(notesDTO);
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<Note> createNote( @RequestHeader("X-User-Id") String userId, @RequestBody Note note) {
        try {
            note.setId(null);
            note.setUser(userRepository.findById(Long.parseLong(userId)).get());
            Note savedNote = noteService.saveNote(note);
            return ResponseEntity.ok(savedNote);
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
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

