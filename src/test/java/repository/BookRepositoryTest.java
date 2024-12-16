package repository;

import entity.Author;
import entity.Book;
import entity.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryTest extends BaseRepositoryTest {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        super.setUp();
        bookRepository = new BookRepository(getDataSource());
        authorRepository = new AuthorRepository(getDataSource());
        genreRepository = new GenreRepository(getDataSource());
    }

    @Test
    void findById_ExistingBook_ReturnsBookWithAuthorAndGenres() {
        Long bookId = 1L; // Евгений Онегин

        var bookOptional = bookRepository.findById(bookId);

        assertTrue(bookOptional.isPresent());
        Book book = bookOptional.get();
        assertEquals("Евгений Онегин", book.getTitle());
        assertEquals("978-5-17-123456-1", book.getIsbn());
        assertEquals(1833, book.getPublicationYear());

        assertNotNull(book.getAuthor());
        assertEquals("Александр", book.getAuthor().getFirstName());
        assertEquals("Пушкин", book.getAuthor().getLastName());

        assertNotNull(book.getGenres());
        assertEquals(2, book.getGenres().size());
        assertTrue(book.getGenres().stream()
                .anyMatch(genre -> genre.getName().equals("Роман")));
        assertTrue(book.getGenres().stream()
                .anyMatch(genre -> genre.getName().equals("Поэзия")));
    }

    @Test
    void findById_NonExistingBook_ReturnsEmpty() {
        Long nonExistingId = 999L;

        var bookOptional = bookRepository.findById(nonExistingId);

        assertTrue(bookOptional.isEmpty());
    }

    @Test
    void save_NewBook_SavesSuccessfully() {
        Author author = authorRepository.findById(1L).orElseThrow();
        Genre genre1 = genreRepository.findById(1L).orElseThrow();
        Genre genre2 = genreRepository.findById(2L).orElseThrow();

        Book newBook = new Book();
        newBook.setTitle("Капитанская дочка");
        newBook.setIsbn("978-5-17-123456-4");
        newBook.setPublicationYear(1836);
        newBook.setAuthor(author);
        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        newBook.setGenres(genres);

        Book savedBook = bookRepository.save(newBook);

        assertNotNull(savedBook.getId());
        var foundBook = bookRepository.findById(savedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("Капитанская дочка", foundBook.get().getTitle());
        assertEquals(2, foundBook.get().getGenres().size());
        assertEquals(author.getId(), foundBook.get().getAuthor().getId());
    }

    @Test
    void save_ExistingBook_UpdatesSuccessfully() {
        Book book = bookRepository.findById(1L).orElseThrow();
        String newTitle = "Евгений Онегин (обновленное издание)";
        book.setTitle(newTitle);

        Book updatedBook = bookRepository.save(book);

        assertEquals(1L, updatedBook.getId());
        var foundBook = bookRepository.findById(1L);
        assertTrue(foundBook.isPresent());
        assertEquals(newTitle, foundBook.get().getTitle());
        assertNotNull(foundBook.get().getAuthor());
        assertFalse(foundBook.get().getGenres().isEmpty());
    }

    @Test
    void save_UpdateBookGenres_UpdatesSuccessfully() {
        Book book = bookRepository.findById(1L).orElseThrow();
        Genre newGenre = genreRepository.findById(3L).orElseThrow(); // Драма
        Set<Genre> newGenres = new HashSet<>();
        newGenres.add(newGenre);
        book.setGenres(newGenres);

        Book updatedBook = bookRepository.save(book);

        var foundBook = bookRepository.findById(updatedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals(1, foundBook.get().getGenres().size());
        assertTrue(foundBook.get().getGenres().stream()
                .anyMatch(genre -> genre.getName().equals("Драма")));
    }

    @Test
    void save_UpdateBookAuthor_UpdatesSuccessfully() {
        Book book = bookRepository.findById(1L).orElseThrow();
        Author newAuthor = authorRepository.findById(2L).orElseThrow(); // Толстой
        book.setAuthor(newAuthor);

        Book updatedBook = bookRepository.save(book);

        var foundBook = bookRepository.findById(updatedBook.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("Лев", foundBook.get().getAuthor().getFirstName());
        assertEquals("Толстой", foundBook.get().getAuthor().getLastName());
    }

    @Test
    void save_NewBookWithoutAuthor_ThrowsException() {
        Book newBook = new Book();
        newBook.setTitle("Книга без автора");

        assertThrows(RuntimeException.class, () -> bookRepository.save(newBook));
    }
}
