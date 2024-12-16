package repository;

import entity.Author;
import entity.Book;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class AuthorRepository extends BaseRepository {
    public AuthorRepository() {
        super();
    }

    public AuthorRepository(DataSource dataSource) {
        super(dataSource);
    }

    public Optional<Author> findById(Long id) {
        String sql = """
            SELECT a.id, a.first_name, a.last_name, a.biography
            FROM authors a
            WHERE a.id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Author author = new Author();
                author.setId(rs.getLong("id"));
                author.setFirstName(rs.getString("first_name"));
                author.setLastName(rs.getString("last_name"));
                author.setBiography(rs.getString("biography"));

                // Загружаем связанные книги
                author.setBooks(findBooksByAuthorId(conn, id));

                return Optional.of(author);
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding author by id: " + id, e);
        }
    }

    private Set<Book> findBooksByAuthorId(Connection conn, Long authorId) throws SQLException {
        String sql = """
            SELECT b.id, b.title, b.isbn, b.publication_year
            FROM books b
            WHERE b.author_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, authorId);
            ResultSet rs = stmt.executeQuery();

            Set<Book> books = new HashSet<>();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("id"));
                book.setTitle(rs.getString("title"));
                book.setIsbn(rs.getString("isbn"));
                book.setPublicationYear(rs.getInt("publication_year"));
                books.add(book);
            }
            return books;
        }
    }

    public Author save(Author author) {
        if (author.getId() == null) {
            return insert(author);
        }
        return update(author);
    }

    private Author insert(Author author) {
        String sql = """
            INSERT INTO authors (first_name, last_name, biography)
            VALUES (?, ?, ?)
            RETURNING id
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.setString(3, author.getBiography());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                author.setId(rs.getLong("id"));
            }

            return author;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting author", e);
        }
    }

    private Author update(Author author) {
        String sql = """
            UPDATE authors
            SET first_name = ?, last_name = ?, biography = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.setString(3, author.getBiography());
            stmt.setLong(4, author.getId());

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Author not found with id: " + author.getId());
            }

            return author;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating author", e);
        }
    }

    public List<Author> findAll() {
        String sql = "SELECT id, first_name, last_name, biography FROM authors";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Author> authors = new ArrayList<>();
            while (rs.next()) {
                Author author = new Author();
                author.setId(rs.getLong("id"));
                author.setFirstName(rs.getString("first_name"));
                author.setLastName(rs.getString("last_name"));
                author.setBiography(rs.getString("biography"));
                authors.add(author);
            }

            return authors;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all authors", e);
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM authors WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int deleted = stmt.executeUpdate();

            if (deleted == 0) {
                throw new RuntimeException("Author not found with id: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting author with id: " + id, e);
        }
    }
}
