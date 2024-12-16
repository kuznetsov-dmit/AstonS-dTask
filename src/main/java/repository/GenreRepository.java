package repository;

import entity.Book;
import entity.Genre;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class GenreRepository extends BaseRepository {
    public GenreRepository() {
        super();
    }

    GenreRepository(DataSource dataSource) {
        super(dataSource);
    }

    public Optional<Genre> findById(Long id) {
        String sql = """
            SELECT g.id, g.name, g.description
            FROM genres g
            WHERE g.id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
                genre.setDescription(rs.getString("description"));

                // Загружаем связанные книги
                genre.setBooks(findBooksByGenreId(conn, id));

                return Optional.of(genre);
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding genre by id: " + id, e);
        }
    }

    private Set<Book> findBooksByGenreId(Connection conn, Long genreId) throws SQLException {
        String sql = """
            SELECT b.id, b.title, b.isbn, b.publication_year
            FROM books b
            JOIN books_genres bg ON b.id = bg.book_id
            WHERE bg.genre_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, genreId);
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

    public List<Genre> findAll() {
        String sql = """
            SELECT g.id, g.name, g.description
            FROM genres g
            ORDER BY g.name
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Genre> genres = new ArrayList<>();
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
                genre.setDescription(rs.getString("description"));
                genres.add(genre);
            }

            return genres;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all genres", e);
        }
    }

    public Genre save(Genre genre) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (genre.getId() == null) {
                    genre = insert(conn, genre);
                } else {
                    genre = update(conn, genre);
                }

                // Обновляем связи с книгами
                updateGenreBooks(conn, genre);

                conn.commit();
                return genre;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving genre", e);
        }
    }

    private Genre insert(Connection conn, Genre genre) throws SQLException {
        String sql = """
            INSERT INTO genres (name, description)
            VALUES (?, ?)
            RETURNING id
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genre.getName());
            stmt.setString(2, genre.getDescription());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                genre.setId(rs.getLong("id"));
            }

            return genre;
        }
    }

    private Genre update(Connection conn, Genre genre) throws SQLException {
        String sql = """
            UPDATE genres
            SET name = ?, description = ?
            WHERE id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genre.getName());
            stmt.setString(2, genre.getDescription());
            stmt.setLong(3, genre.getId());

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Genre not found with id: " + genre.getId());
            }

            return genre;
        }
    }

    private void updateGenreBooks(Connection conn, Genre genre) throws SQLException {
        // Удаляем старые связи
        String deleteSql = "DELETE FROM books_genres WHERE genre_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setLong(1, genre.getId());
            stmt.executeUpdate();
        }

        // Добавляем новые связи
        String insertSql = "INSERT INTO books_genres (genre_id, book_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (Book book : genre.getBooks()) {
                stmt.setLong(1, genre.getId());
                stmt.setLong(2, book.getId());
                stmt.executeUpdate();
            }
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM genres WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int deleted = stmt.executeUpdate();

            if (deleted == 0) {
                throw new RuntimeException("Genre not found with id: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting genre with id: " + id, e);
        }
    }

    public Optional<Genre> findByName(String name) {
        String sql = """
            SELECT g.id, g.name, g.description
            FROM genres g
            WHERE g.name = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
                genre.setDescription(rs.getString("description"));
                genre.setBooks(findBooksByGenreId(conn, genre.getId()));

                return Optional.of(genre);
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding genre by name: " + name, e);
        }
    }

}