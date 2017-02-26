package com.example;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestOperations;

@Repository
public class TaskRepository {

	private final RestOperations restOperations;
	private final String resourcesUrl;
	private final String resourceUrlTemplate;

	TaskRepository(RestOperations restOperations,
			@Value("${api.url}/tasks") String resourcesUrl) {
		this.restOperations = restOperations;
		this.resourcesUrl = resourcesUrl;
		this.resourceUrlTemplate = resourcesUrl + "/{id}";
	}

	public List<Task> findAll() {
		return Arrays.asList(restOperations.getForObject(resourcesUrl, Task[].class));
	}

	public Task findOne(long id) {
		return restOperations.getForObject(resourceUrlTemplate, Task.class, id);
	}

	public void save(Task task) {
		if (task.getId() == null) {
			restOperations.postForLocation(resourcesUrl, task);
		} else {
			restOperations.put(resourceUrlTemplate, task, task.getId());
		}
	}

	public void remove(long id) {
		restOperations.delete(resourceUrlTemplate, id);
	}

}
