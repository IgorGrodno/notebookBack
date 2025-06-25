package com.example.notebookback.models.DTOs;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NoteDTO {
    private Long id;
    private LocalDateTime date;
    private String title;
    private String text;
}
