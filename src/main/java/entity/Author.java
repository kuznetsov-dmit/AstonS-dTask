package entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Author {
    private Long id;
    private String firstName;
    private String lastName;
    private String biography;
    private Set<Book> books = new HashSet<>();

    public Author() {
    }

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addBook(Book book) {
        if (book != null) {
            books.add(book);
            book.setAuthor(this);
        }
    }

    public void removeBook(Book book) {
        if (book != null) {
            books.remove(book);
            book.setAuthor(null);
        }
    }

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

    public Set<Book> getBooks() {
        return new HashSet<>(books);
    }

    public void setBooks(Set<Book> books) {
        this.books = new HashSet<>(books);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
