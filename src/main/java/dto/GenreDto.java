package dto;

import java.util.HashSet;
import java.util.Set;

public class GenreDto {
    private Long id;
    private String name;
    private String description;
    private Set<BookShortDto> books = new HashSet<>();

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<BookShortDto> getBooks() {
        return books;
    }

    public void setBooks(Set<BookShortDto> books) {
        this.books = books;
    }
}
