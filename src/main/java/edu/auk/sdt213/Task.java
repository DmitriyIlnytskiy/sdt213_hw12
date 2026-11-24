package edu.auk.sdt213;

public class Task {
    private final int id;
    private String title;
    private boolean completed;

    public Task(int id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Простая сериализация в JSON без сторонних библиотек
    public String toJson() {
        String escapedTitle = title.replace("\\", "\\\\").replace("\"", "\\\"");
        return String.format("{\"id\":%d,\"title\":\"%s\",\"completed\":%b}", id, escapedTitle, completed);
    }
}