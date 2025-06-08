package com.example.notebookback.controllers;

import com.example.notebookback.models.DTOs.NotesDTO;
import com.example.notebookback.models.ntities.Note;
import com.example.notebookback.services.NoteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<NotesDTO> getNotes(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        NotesDTO notesDTO = noteService.searchNotes(title, startDate, endDate, page, size);
        return ResponseEntity.ok(notesDTO);
    }


    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        note.setId(null);
        Note savedNote = noteService.saveNote(note);
        return ResponseEntity.ok(savedNote);

    }
}

