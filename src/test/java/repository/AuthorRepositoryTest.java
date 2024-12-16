package repository;

import entity.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorRepositoryTest extends BaseRepositoryTest {

    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        super.setUp();
        authorRepository = new AuthorRepository(getDataSource());
    }

    @Test
    void findById_ExistingAuthor_ReturnsAuthor() {
        // Given
        Long authorId = 1L;

        // When
        var authorOptional = authorRepository.findById(authorId);

        // Then
        assertTrue(authorOptional.isPresent());
        var author = authorOptional.get();
        assertEquals("Александр", author.getFirstName());
        assertEquals("Пушкин", author.getLastName());
        assertNotNull(author.getBooks());
        assertFalse(author.getBooks().isEmpty());
    }

    @Test
    void findById_NonExistingAuthor_ReturnsEmpty() {
        // Given
        Long nonExistingId = 999L;

        // When
        var authorOptional = authorRepository.findById(nonExistingId);

        // Then
        assertTrue(authorOptional.isEmpty());
    }

    @Test
    void save_NewAuthor_SavesSuccessfully() {
        // Given
        Author newAuthor = new Author();
        newAuthor.setFirstName("Михаил");
        newAuthor.setLastName("Булгаков");
        newAuthor.setBiography("Русский писатель");

        // When
        Author savedAuthor = authorRepository.save(newAuthor);

        // Then
        assertNotNull(savedAuthor.getId());
        var foundAuthor = authorRepository.findById(savedAuthor.getId());
        assertTrue(foundAuthor.isPresent());
        assertEquals("Михаил", foundAuthor.get().getFirstName());
        assertEquals("Булгаков", foundAuthor.get().getLastName());
    }

    @Test
    void save_ExistingAuthor_UpdatesSuccessfully() {
        // Given
        var authorOptional = authorRepository.findById(1L);
        assertTrue(authorOptional.isPresent());
        Author author = authorOptional.get();
        author.setBiography("Обновленная биография");

        // When
        Author updatedAuthor = authorRepository.save(author);

        // Then
        assertEquals(1L, updatedAuthor.getId());
        var foundAuthor = authorRepository.findById(1L);
        assertTrue(foundAuthor.isPresent());
        assertEquals("Обновленная биография", foundAuthor.get().getBiography());
    }
}
