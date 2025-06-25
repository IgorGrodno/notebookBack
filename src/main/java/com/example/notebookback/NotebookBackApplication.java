package com.example.notebookback;

import com.example.notebookback.models.ntities.ERole;
import com.example.notebookback.models.ntities.Role;
import com.example.notebookback.models.ntities.User;
import com.example.notebookback.repositories.NoteRepository;
import com.example.notebookback.repositories.RoleRepository;
import com.example.notebookback.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class NotebookBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotebookBackApplication.class, args);
	}
	@Bean
	CommandLineRunner init(NoteRepository noteRepository, UserRepository userRepository, RoleRepository roleRepository,
						   PasswordEncoder encoder) {
		return args -> {
			if(userRepository.findAll().isEmpty()){
				roleRepository.deleteAll();
				roleRepository.save(new Role(ERole.ROLE_ADMIN));
				roleRepository.save(new Role(ERole.ROLE_USER));
				User user = new User();
				user.setUsername("admin");
				user.setPassword(encoder.encode("admin"));
				Set<Role> roles = new HashSet<>();
				roles.add(roleRepository.findByName(ERole.valueOf("ROLE_ADMIN")).get());
				roles.add(roleRepository.findByName(ERole.valueOf("ROLE_USER")).get());
				user.setRoles(roles);
				userRepository.save(user);
			}

		};
	}
}
