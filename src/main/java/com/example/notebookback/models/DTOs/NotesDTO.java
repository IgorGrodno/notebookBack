package com.example.notebookback.models.DTOs;

import com.example.notebookback.models.ntities.Note;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NotesDTO {
    private List<NoteDTO> notes;
    private long totalNotes;
    private int pageSize;
    private int pageIndex;
    private int totalPages;
}
