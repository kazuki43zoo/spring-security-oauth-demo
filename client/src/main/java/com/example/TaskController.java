package com.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/tasks")
@Controller
public class TaskController {

	private final TaskRepository repository;

	TaskController(TaskRepository repository) {
		this.repository = repository;
	}

	@ModelAttribute
	TaskForm setUpForm() {
		return new TaskForm();
	}

	@GetMapping
	String list(Model model) {
		List<Task> taskList = repository.findAll();
		model.addAttribute(taskList);
		return "task/list";
	}

	@PostMapping
	String create(@Validated TaskForm form, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return list(model);
		}
		Task task = new Task();
		BeanUtils.copyProperties(form, task);
		repository.save(task);
		return "redirect:/tasks";
	}

	@GetMapping("{id}")
	String detail(@PathVariable long id, TaskForm form, Model model) {
		Task task = repository.findOne(id);
		BeanUtils.copyProperties(task, form);
		model.addAttribute(task);
		return "task/detail";
	}

	@PostMapping(path = "{id}", params = "update")
	String update(@PathVariable long id, @Validated TaskForm form, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "task/detail";
		}
		Task task = new Task();
		BeanUtils.copyProperties(form, task);
		repository.save(task);
		redirectAttributes.addAttribute("id", id);
		return "redirect:/tasks/{id}";
	}

	@PostMapping(path = "{id}", params = "delete")
	String delete(@PathVariable long id) {
		repository.remove(id);
		return "redirect:/tasks";
	}

	static class TaskForm {
		private static final String DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss";

		private Long id;
		@NotEmpty private String title;
		private String detail;
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate deadline;
		private boolean finished;
		@DateTimeFormat(pattern = DATE_TIME_FORMAT) private LocalDateTime createdAt;
		@DateTimeFormat(pattern = DATE_TIME_FORMAT) private LocalDateTime updatedAt;
		private long version;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public LocalDate getDeadline() {
			return deadline;
		}

		public void setDeadline(LocalDate deadline) {
			this.deadline = deadline;
		}

		public boolean isFinished() {
			return finished;
		}

		public void setFinished(boolean finished) {
			this.finished = finished;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}

		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}

		public long getVersion() {
			return version;
		}

		public void setVersion(long version) {
			this.version = version;
		}
	}

}
