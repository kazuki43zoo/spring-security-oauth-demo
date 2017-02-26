package com.example;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/api/tasks")
@RestController
public class TaskRestController {

	private final TaskRepository repository;

	TaskRestController(TaskRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	List<Task> getTasks(Principal principal) {
		return repository.findAll(extractUsername(principal));
	}

	@PostMapping
	ResponseEntity<Void> postTask(@RequestBody Task task, Principal principal, UriComponentsBuilder uriBuilder) {
		task.setUsername(extractUsername(principal));
		repository.save(task);
		URI createdTaskUri = relativeTo(uriBuilder)
				.withMethodCall(on(TaskRestController.class).getTask(task.getId(), principal)).build().encode().toUri();
		return ResponseEntity.created(createdTaskUri).build();
	}

	@GetMapping("{id}")
	Task getTask(@PathVariable long id, Principal principal) {
		return repository.findOne(id);
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void putTask(@PathVariable long id, @RequestBody Task task, Principal principal) {
		repository.findOne(id);
		task.setId(id);
		repository.save(task);
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteTask(@PathVariable long id, Principal principal) {
		repository.findOne(id);
		repository.remove(id);
	}

	private String extractUsername(Principal principal) {
		return Optional.ofNullable(principal).map(Principal::getName).orElse("none");
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	void handleEmptyResultDataAccessException() {
		// NOP
	}

}
