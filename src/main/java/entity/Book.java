package entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Book {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private Author author;
    private Set<Genre> genres = new HashSet<>();

    public Book() {
    }

    public Book(String title, String isbn) {
        this.title = title;
        this.isbn = isbn;
    }

    public void addGenre(Genre genre) {
        if (genre != null) {
            genres.add(genre);
            genre.getBooks().add(this);
        }
    }

    public void removeGenre(Genre genre) {
        if (genre != null) {
            genres.remove(genre);
            genre.getBooks().remove(this);
        }
    }

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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<Genre> getGenres() {
        return new HashSet<>(genres);
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = new HashSet<>(genres);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
