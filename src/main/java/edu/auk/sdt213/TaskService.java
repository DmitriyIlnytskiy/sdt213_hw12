package edu.auk.sdt213;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskService {
    private final Map<Integer, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public TaskService() {
        // Добавляем пару стартовых задач для наглядности
        createTask("Prepare SDT213 web service demo");
        createTask("Push code to private GitHub repo");
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task createTask(String title) {
        int id = idGenerator.getAndIncrement();
        Task task = new Task(id, title, false);
        tasks.put(id, task);
        return task;
    }

    public boolean deleteTask(int id) {
        return tasks.remove(id) != null;
    }

    public String toJsonArray() {
        StringBuilder sb = new StringBuilder("[");
        List<Task> list = getAllTasks();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toJson());
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}