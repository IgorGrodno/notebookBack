package com.example.notebookback.models.ntities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notes")
@NoArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private LocalDateTime date;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String text;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
