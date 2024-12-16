package repository;

import entity.Author;
import entity.Book;
import entity.Genre;
import exception.DatabaseException;
import exception.EntityNotFoundException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class BookRepository extends BaseRepository {
    public BookRepository() {
        super();
    }

    BookRepository(DataSource dataSource) {
        super(dataSource);
    }

    public Optional<Book> findById(Long id) {
        String sql = """
            SELECT b.id, b.title, b.isbn, b.publication_year, b.author_id,
                   a.first_name, a.last_name, a.biography
            FROM books b
            JOIN authors a ON b.author_id = a.id
            WHERE b.id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("id"));
                book.setTitle(rs.getString("title"));
                book.setIsbn(rs.getString("isbn"));
                book.setPublicationYear(rs.getInt("publication_year"));

                Author author = new Author();
                author.setId(rs.getLong("author_id"));
                author.setFirstName(rs.getString("first_name"));
                author.setLastName(rs.getString("last_name"));
                author.setBiography(rs.getString("biography"));

                book.setAuthor(author);
                book.setGenres(findGenresByBookId(conn, id));

                return Optional.of(book);
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding book by id: " + id, e);
        }
    }

    private Set<Genre> findGenresByBookId(Connection conn, Long bookId) throws SQLException {
        String sql = """
            SELECT g.id, g.name, g.description
            FROM genres g
            JOIN books_genres bg ON g.id = bg.genre_id
            WHERE bg.book_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookId);
            ResultSet rs = stmt.executeQuery();

            Set<Genre> genres = new HashSet<>();
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
                genre.setDescription(rs.getString("description"));
                genres.add(genre);
            }
            return genres;
        }
    }

    public Book save(Book book) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (book.getId() == null) {
                    book = insert(conn, book);
                } else {
                    book = update(conn, book);
                }

                // Обновляем связи с жанрами
                updateBookGenres(conn, book);

                conn.commit();
                return book;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving book", e);
        }
    }

    private Book insert(Connection conn, Book book) throws SQLException {
        String sql = """
            INSERT INTO books (title, isbn, publication_year, author_id)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn());
            stmt.setInt(3, book.getPublicationYear());
            stmt.setLong(4, book.getAuthor().getId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                book.setId(rs.getLong("id"));
            }

            return book;
        }
    }

    private Book update(Connection conn, Book book) throws SQLException {
        String sql = """
            UPDATE books
            SET title = ?, isbn = ?, publication_year = ?, author_id = ?
            WHERE id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn());
            stmt.setInt(3, book.getPublicationYear());
            stmt.setLong(4, book.getAuthor().getId());
            stmt.setLong(5, book.getId());

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Book not found with id: " + book.getId());
            }

            return book;
        }
    }

    private void updateBookGenres(Connection conn, Book book) throws SQLException {
        // Удаляем старые связи
        String deleteSql = "DELETE FROM books_genres WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setLong(1, book.getId());
            stmt.executeUpdate();
        }

        // Добавляем новые связи
        String insertSql = "INSERT INTO books_genres (book_id, genre_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (Genre genre : book.getGenres()) {
                stmt.setLong(1, book.getId());
                stmt.setLong(2, genre.getId());
                stmt.executeUpdate();
            }
        }
    }
    public List<Book> findAll() throws DatabaseException {
        String sql = """
            SELECT DISTINCT b.id, b.title, b.isbn, b.publication_year,
                   a.id as author_id, a.first_name, a.last_name
            FROM books b
            JOIN authors a ON b.author_id = a.id
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            Map<Long, Book> books = new HashMap<>();

            while (rs.next()) {
                Long bookId = rs.getLong("id");
                Book book = books.get(bookId);

                if (book == null) {
                    book = new Book();
                    book.setId(bookId);
                    book.setTitle(rs.getString("title"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setPublicationYear(rs.getInt("publication_year"));

                    Author author = new Author();
                    author.setId(rs.getLong("author_id"));
                    author.setFirstName(rs.getString("first_name"));
                    author.setLastName(rs.getString("last_name"));
                    book.setAuthor(author);

                    books.put(bookId, book);
                }

                // Загружаем жанры для каждой книги
                book.setGenres(findGenresByBookId(conn, bookId));
            }

            return new ArrayList<>(books.values());
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all books", e);
        }
    }

    public void deleteById(Long id) throws DatabaseException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int deleted = stmt.executeUpdate();

            if (deleted == 0) {
                throw new EntityNotFoundException("Book", id.toString());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting book with id: " + id, e);
        }
    }

}
