package dto;

import java.util.HashSet;
import java.util.Set;

public class BookDto {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private AuthorShortDto author;
    private Set<GenreShortDto> genres = new HashSet<>();

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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public AuthorShortDto getAuthor() {
        return author;
    }

    public void setAuthor(AuthorShortDto author) {
        this.author = author;
    }

    public Set<GenreShortDto> getGenres() {
        return genres;
    }

    public void setGenres(Set<GenreShortDto> genres) {
        this.genres = genres;
    }
}
