package service.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.AuthorRepository;
import repository.BookRepository;
import repository.GenreRepository;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ServiceFactoryTest {

    @BeforeEach
    void setUp() {
        ServiceFactory.setInstance(null);
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        // Given
        ServiceFactory mockFactory = new ServiceFactory(
                mock(AuthorRepository.class),
                mock(BookRepository.class),
                mock(GenreRepository.class)
        );
        ServiceFactory.setInstance(mockFactory);

        // When
        ServiceFactory instance1 = ServiceFactory.getInstance();
        ServiceFactory instance2 = ServiceFactory.getInstance();

        // Then
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    void getServices_ReturnsNonNullServices() {
        // Given
        ServiceFactory factory = new ServiceFactory(
                mock(AuthorRepository.class),
                mock(BookRepository.class),
                mock(GenreRepository.class)
        );

        // When/Then
        assertNotNull(factory.getAuthorService());
        assertNotNull(factory.getBookService());
        assertNotNull(factory.getGenreService());
    }

    @Test
    void getServices_ReturnsSameServiceInstances() {
        // Given
        ServiceFactory factory = new ServiceFactory(
                mock(AuthorRepository.class),
                mock(BookRepository.class),
                mock(GenreRepository.class)
        );

        // When
        var authorService1 = factory.getAuthorService();
        var authorService2 = factory.getAuthorService();
        var bookService1 = factory.getBookService();
        var bookService2 = factory.getBookService();
        var genreService1 = factory.getGenreService();
        var genreService2 = factory.getGenreService();

        // Then
        assertSame(authorService1, authorService2);
        assertSame(bookService1, bookService2);
        assertSame(genreService1, genreService2);
    }
}