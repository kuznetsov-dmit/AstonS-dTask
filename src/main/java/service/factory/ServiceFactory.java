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
    private static final ServiceFactory INSTANCE = new ServiceFactory();

    private final AuthorService authorService;
    private final BookService bookService;
    private final GenreService genreService;

    private ServiceFactory() {
        AuthorRepository authorRepository = new AuthorRepository();
        BookRepository bookRepository = new BookRepository();
        GenreRepository genreRepository = new GenreRepository();

        AuthorMapper authorMapper = AuthorMapper.INSTANCE;
        BookMapper bookMapper = BookMapper.INSTANCE;
        GenreMapper genreMapper = GenreMapper.INSTANCE;

        this.authorService = new AuthorService(authorRepository, authorMapper);
        this.bookService = new BookService(bookRepository, authorRepository,
                genreRepository, bookMapper);
        this.genreService = new GenreService(genreRepository, genreMapper);
    }

    public static ServiceFactory getInstance() {
        return INSTANCE;
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
