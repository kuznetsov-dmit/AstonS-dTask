package repository;

import entity.Book;
import entity.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreRepositoryTest extends BaseRepositoryTest {

    private GenreRepository genreRepository;
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        super.setUp();
        genreRepository = new GenreRepository(getDataSource());
        bookRepository = new BookRepository(getDataSource());
    }

    @Test
    void findById_ExistingGenre_ReturnsGenreWithBooks() {
        Long genreId = 1L;

        var genreOptional = genreRepository.findById(genreId);

        assertTrue(genreOptional.isPresent());
        Genre genre = genreOptional.get();
        assertEquals("Роман", genre.getName());

        assertNotNull(genre.getBooks());
        assertFalse(genre.getBooks().isEmpty());
        assertTrue(genre.getBooks().stream()
                .anyMatch(book -> book.getTitle().equals("Война и мир")));
    }

    @Test
    void findById_NonExistingGenre_ReturnsEmpty() {
        Long nonExistingId = 999L;

        var genreOptional = genreRepository.findById(nonExistingId);

        assertTrue(genreOptional.isEmpty());
    }

    @Test
    void findByName_ExistingGenre_ReturnsGenre() {
        String genreName = "Поэзия";

        var genreOptional = genreRepository.findByName(genreName);

        assertTrue(genreOptional.isPresent());
        assertEquals(genreName, genreOptional.get().getName());
    }

    @Test
    void findByName_NonExistingGenre_ReturnsEmpty() {
        String nonExistingName = "Несуществующий жанр";

        var genreOptional = genreRepository.findByName(nonExistingName);

        assertTrue(genreOptional.isEmpty());
    }

    @Test
    void save_NewGenre_SavesSuccessfully() {
        Genre newGenre = new Genre();
        newGenre.setName("Фантастика");
        newGenre.setDescription("Научная фантастика и фэнтези");

        Genre savedGenre = genreRepository.save(newGenre);

        assertNotNull(savedGenre.getId());
        var foundGenre = genreRepository.findById(savedGenre.getId());
        assertTrue(foundGenre.isPresent());
        assertEquals("Фантастика", foundGenre.get().getName());
    }

    @Test
    void save_ExistingGenre_UpdatesSuccessfully() {
        var genreOptional = genreRepository.findById(1L);
        assertTrue(genreOptional.isPresent());
        Genre genre = genreOptional.get();
        String newDescription = "Обновленное описание романа";
        genre.setDescription(newDescription);

        Genre updatedGenre = genreRepository.save(genre);

        assertEquals(1L, updatedGenre.getId());
        var foundGenre = genreRepository.findById(1L);
        assertTrue(foundGenre.isPresent());
        assertEquals(newDescription, foundGenre.get().getDescription());
        assertFalse(foundGenre.get().getBooks().isEmpty());
    }

    @Test
    void save_GenreWithNewBook_UpdatesSuccessfully() {
        Genre genre = genreRepository.findById(1L).orElseThrow();
        Book book = bookRepository.findById(1L).orElseThrow();
        genre.getBooks().add(book);

        Genre updatedGenre = genreRepository.save(genre);

        var foundGenre = genreRepository.findById(updatedGenre.getId());
        assertTrue(foundGenre.isPresent());
        assertTrue(foundGenre.get().getBooks().stream()
                .anyMatch(b -> b.getId().equals(book.getId())));
    }

    @Test
    void save_GenreWithoutName_ThrowsException() {
        Genre genre = new Genre();
        genre.setDescription("Описание без названия");

        assertThrows(RuntimeException.class, () -> genreRepository.save(genre));
    }

    @Test
    void save_DuplicateGenreName_ThrowsException() {
        Genre newGenre = new Genre();
        newGenre.setName("Роман"); // Уже существует в БД
        newGenre.setDescription("Новое описание");

        assertThrows(RuntimeException.class, () -> genreRepository.save(newGenre));
    }

    @Test
    void deleteById_ExistingGenre_DeletesSuccessfully() {
        Long genreId = 3L; // Драма

        genreRepository.deleteById(genreId);

        var deletedGenre = genreRepository.findById(genreId);
        assertTrue(deletedGenre.isEmpty());
    }

    @Test
    void deleteById_NonExistingGenre_ThrowsException() {
        Long nonExistingId = 999L;

        assertThrows(RuntimeException.class, () -> genreRepository.deleteById(nonExistingId));
    }
}
