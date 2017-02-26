package com.example;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class TaskRepository {
	private final NamedParameterJdbcOperations jdbcOperations;

	public TaskRepository(NamedParameterJdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}

	public List<Task> findAll(String username) {
		return jdbcOperations.query(
				"SELECT id, username, title, detail, deadline, finished, created_at, updated_at, version FROM tasks WHERE username = :username ORDER BY deadline DESC, id DESC",
				new MapSqlParameterSource("username", username), new BeanPropertyRowMapper<>(Task.class));
	}

	@PostAuthorize("returnObject.username == authentication.name")
	public Task findOne(long id) {
		return jdbcOperations.queryForObject(
				"SELECT id, username, title, detail, deadline, finished, created_at, updated_at, version FROM tasks WHERE id = :id",
				new MapSqlParameterSource("id", id), new BeanPropertyRowMapper<>(Task.class));
	}

	public void save(Task task) {
		if (task.getId() == null) {
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			jdbcOperations.update(
					"INSERT INTO tasks (username, title, detail, deadline, finished) VALUES(:username, :title, :detail, :deadline, :finished)",
					new BeanPropertySqlParameterSource(task), holder);
			task.setId(holder.getKey().longValue());
		} else {
			jdbcOperations.update(
					"UPDATE tasks SET title = :title, detail = :detail, deadline = :deadline, finished = :finished, updated_at = SYSTIMESTAMP, version = version + 1 WHERE id = :id",
					new BeanPropertySqlParameterSource(task));
		}
	}

	public void remove(long id) {
		jdbcOperations.update("DELETE FROM tasks WHERE id = :id", new MapSqlParameterSource("id", id));
	}

}
