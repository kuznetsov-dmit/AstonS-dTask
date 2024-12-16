package dto;

import java.util.HashSet;
import java.util.Set;

public class AuthorDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String biography;
    private Set<BookShortDto> books = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Set<BookShortDto> getBooks() {
        return books;
    }

    public void setBooks(Set<BookShortDto> books) {
        this.books = books;
    }
}
