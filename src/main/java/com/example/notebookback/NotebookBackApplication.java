package com.example.notebookback;

import com.example.notebookback.repositories.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NotebookBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotebookBackApplication.class, args);
	}
	@Bean
	CommandLineRunner init(NoteRepository noteRepository) {
		return args -> {
			noteRepository.deleteAll();
		};
	}
}
