package service.factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ServiceFactoryTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        ServiceFactory instance1 = ServiceFactory.getInstance();
        ServiceFactory instance2 = ServiceFactory.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    void getServices_ReturnsNonNullServices() {
        ServiceFactory factory = ServiceFactory.getInstance();

        assertNotNull(factory.getAuthorService());
        assertNotNull(factory.getBookService());
        assertNotNull(factory.getGenreService());
    }

    @Test
    void getServices_ReturnsSameServiceInstances() {
        ServiceFactory factory = ServiceFactory.getInstance();

        var authorService1 = factory.getAuthorService();
        var authorService2 = factory.getAuthorService();
        var bookService1 = factory.getBookService();
        var bookService2 = factory.getBookService();
        var genreService1 = factory.getGenreService();
        var genreService2 = factory.getGenreService();

        assertSame(authorService1, authorService2);
        assertSame(bookService1, bookService2);
        assertSame(genreService1, genreService2);
    }
}