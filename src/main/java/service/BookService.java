package service;

import dto.BookDto;
import dto.GenreShortDto;
import entity.Author;
import entity.Book;
import entity.Genre;
import exception.DatabaseException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import mapper.BookMapper;
import repository.AuthorRepository;
import repository.BookRepository;
import repository.GenreRepository;

import java.util.*;
import java.util.stream.Collectors;

public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookMapper bookMapper;

    public BookService(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            GenreRepository genreRepository,
            BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookMapper = bookMapper;
    }

    public BookDto findById(Long id) throws EntityNotFoundException {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Book", id.toString()));
    }

    public BookDto save(BookDto bookDto) throws EntityNotFoundException, ValidationException {
        validateBookDto(bookDto);

        // Проверяем существование автора
        Author author = authorRepository.findById(bookDto.getAuthor().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Author",
                        bookDto.getAuthor().getId() != null ? bookDto.getAuthor().getId().toString() : "null"
                ));

        // Проверяем существование жанров
        Set<Genre> genres = new HashSet<>();
        for (GenreShortDto genreDto : bookDto.getGenres()) {
            Genre genre = genreRepository.findById(genreDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Genre",
                            genreDto.getId() != null ? genreDto.getId().toString() : "null"
                    ));
            genres.add(genre);
        }

        Book book = bookMapper.toEntity(bookDto);
        book.setAuthor(author);
        book.setGenres(genres);

        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    private void validateBookDto(BookDto bookDto) {
        Map<String, String> errors = new HashMap<>();

        if (bookDto.getTitle() == null || bookDto.getTitle().trim().isEmpty()) {
            errors.put("title", "Book title cannot be empty");
        }

        if (bookDto.getAuthor() == null || bookDto.getAuthor().getId() == null) {
            errors.put("author", "Author must be specified");
        }

        if (bookDto.getGenres() == null || bookDto.getGenres().isEmpty()) {
            errors.put("genres", "At least one genre must be specified");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Book validation failed", errors);
        }
    }

    public List<BookDto> findAll() throws DatabaseException {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws EntityNotFoundException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id.toString()));

        bookRepository.deleteById(id);
    }
}

