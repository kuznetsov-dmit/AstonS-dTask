package service.factory;

import mapper.AuthorMapper;
import mapper.BookMapper;
import mapper.GenreMapper;
import repository.AuthorRepository;
import repository.BookRepository;
import repository.GenreRepository;
import service.AuthorService;
import service.BookService;
import service.GenreService;

public class ServiceFactory {
    private static ServiceFactory INSTANCE = null;

    private final AuthorService authorService;
    private final BookService bookService;
    private final GenreService genreService;

    // Конструктор для production
    ServiceFactory() {
        this(new AuthorRepository(), new BookRepository(), new GenreRepository());
    }

    // Конструктор для тестов
    ServiceFactory(AuthorRepository authorRepository, BookRepository bookRepository, GenreRepository genreRepository) {
        AuthorMapper authorMapper = AuthorMapper.INSTANCE;
        BookMapper bookMapper = BookMapper.INSTANCE;
        GenreMapper genreMapper = GenreMapper.INSTANCE;

        this.authorService = new AuthorService(authorRepository, authorMapper);
        this.bookService = new BookService(bookRepository, authorRepository,
                genreRepository, bookMapper);
        this.genreService = new GenreService(genreRepository, genreMapper);
    }

    public static ServiceFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServiceFactory();
        }
        return INSTANCE;
    }

    // Метод для тестов
    static void setInstance(ServiceFactory instance) {
        INSTANCE = instance;
    }

    public AuthorService getAuthorService() {
        return authorService;
    }

    public BookService getBookService() {
        return bookService;
    }

    public GenreService getGenreService() {
        return genreService;
    }
}
