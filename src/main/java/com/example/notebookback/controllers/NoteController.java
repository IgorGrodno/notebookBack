package com.example.notebookback.controllers;

import com.example.notebookback.models.ntities.Note;
import com.example.notebookback.services.NoteService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotes(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        Page<Note> notesPage = noteService.searchNotes(title, startDate, endDate, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", notesPage.getContent());
        response.put("pageIndex", notesPage.getNumber());
        response.put("pageSize", notesPage.getSize());
        response.put("totalNotes", notesPage.getTotalElements());
        response.put("totalPages", notesPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note savedNote = noteService.saveNote(note);
        return ResponseEntity.ok(savedNote);
    }
}

