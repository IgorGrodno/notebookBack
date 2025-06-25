package com.example.notebookback.utils;

import com.example.notebookback.models.DTOs.NoteDTO;
import com.example.notebookback.models.ntities.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class NoteMapper {

    public static NoteDTO toDto(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setDate(note.getDate());
        dto.setTitle(note.getTitle());
        dto.setText(note.getText());
        return dto;
    }

    public static Page<NoteDTO> toDto(Page<Note> notesPage) {
        return notesPage.map(NoteMapper::toDto);
    }
}

