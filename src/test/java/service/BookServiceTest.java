package service;

import dto.AuthorShortDto;
import dto.BookDto;
import dto.GenreShortDto;
import entity.Author;
import entity.Book;
import entity.Genre;
import exception.EntityNotFoundException;
import exception.ValidationException;
import mapper.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.AuthorRepository;
import repository.BookRepository;
import repository.GenreRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private BookMapper bookMapper;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, authorRepository, genreRepository, bookMapper);
    }

    @Test
    void findById_WhenBookExists_ReturnsBookDto() {
        // Given
        Long bookId = 1L;
        Book book = createTestBook();
        BookDto expectedDto = createTestBookDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        // When
        BookDto result = bookService.findById(bookId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getTitle(), result.getTitle());
        verify(bookRepository).findById(bookId);
        verify(bookMapper).toDto(book);
    }

    @Test
    void findById_WhenBookDoesNotExist_ThrowsException() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(bookId));
        verify(bookRepository).findById(bookId);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void save_ValidBookDto_ReturnsSavedBookDto() {
        // Given
        BookDto inputDto = createTestBookDto();
        Author author = createTestAuthor();
        Genre genre = createTestGenre();
        Book book = createTestBook();
        Book savedBook = createTestBook();
        BookDto expectedDto = createTestBookDto();

        // Мокируем все необходимые вызовы
        when(authorRepository.findById(inputDto.getAuthor().getId())).thenReturn(Optional.of(author));
        // Добавляем мок для жанра
        when(genreRepository.findById(any())).thenReturn(Optional.of(genre));
        when(bookMapper.toEntity(inputDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(expectedDto);

        // When
        BookDto result = bookService.save(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getTitle(), result.getTitle());
        verify(authorRepository).findById(inputDto.getAuthor().getId());
        verify(genreRepository).findById(any()); // Проверяем, что был вызов поиска жанра
        verify(bookMapper).toEntity(inputDto);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(savedBook);
    }

    @Test
    void findAll_ReturnsAllBooks() {
        // Given
        List<Book> books = Arrays.asList(createTestBook(), createTestBook());
        List<BookDto> expectedDtos = Arrays.asList(createTestBookDto(), createTestBookDto());

        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDto(any(Book.class))).thenReturn(expectedDtos.get(0), expectedDtos.get(1));

        // When
        List<BookDto> result = bookService.findAll();

        // Then
        assertEquals(expectedDtos.size(), result.size());
        verify(bookRepository).findAll();
        verify(bookMapper, times(2)).toDto(any(Book.class));
    }

    @Test
    void deleteById_ExistingBook_DeletesSuccessfully() {
        // Given
        Long bookId = 1L;
        Book book = createTestBook();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        bookService.deleteById(bookId);

        // Then
        verify(bookRepository).findById(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    void deleteById_NonExistingBook_ThrowsException() {
        // Given
        Long bookId = 999L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> bookService.deleteById(bookId));
        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).deleteById(any());
    }
    @Test
    void save_BookDtoWithNullTitle_ThrowsValidationException() {
        // Given
        BookDto invalidDto = createTestBookDto();
        invalidDto.setTitle(null);

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookService.save(invalidDto));

        assertTrue(exception.getErrors().containsKey("title"));
        assertEquals("Book title cannot be empty", exception.getErrors().get("title"));
        verifyNoInteractions(authorRepository, genreRepository, bookMapper, bookRepository);
    }

    @Test
    void save_BookDtoWithEmptyTitle_ThrowsValidationException() {
        // Given
        BookDto invalidDto = createTestBookDto();
        invalidDto.setTitle("   ");

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookService.save(invalidDto));

        assertTrue(exception.getErrors().containsKey("title"));
        assertEquals("Book title cannot be empty", exception.getErrors().get("title"));
        verifyNoInteractions(authorRepository, genreRepository, bookMapper, bookRepository);
    }

    @Test
    void save_BookDtoWithNullAuthor_ThrowsValidationException() {
        // Given
        BookDto invalidDto = createTestBookDto();
        invalidDto.setAuthor(null);

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookService.save(invalidDto));

        assertTrue(exception.getErrors().containsKey("author"));
        assertEquals("Author must be specified", exception.getErrors().get("author"));
        verifyNoInteractions(authorRepository, genreRepository, bookMapper, bookRepository);
    }

    @Test
    void save_BookDtoWithAuthorWithoutId_ThrowsValidationException() {
        // Given
        BookDto invalidDto = createTestBookDto();
        AuthorShortDto authorDto = new AuthorShortDto();
        invalidDto.setAuthor(authorDto);

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookService.save(invalidDto));

        assertTrue(exception.getErrors().containsKey("author"));
        assertEquals("Author must be specified", exception.getErrors().get("author"));
        verifyNoInteractions(authorRepository, genreRepository, bookMapper, bookRepository);
    }

    @Test
    void save_BookDtoWithNullGenres_ThrowsValidationException() {
        // Given
        BookDto invalidDto = createTestBookDto();
        invalidDto.setGenres(null);

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookService.save(invalidDto));

        assertTrue(exception.getErrors().containsKey("genres"));
        assertEquals("At least one genre must be specified", exception.getErrors().get("genres"));
        verifyNoInteractions(authorRepository, genreRepository, bookMapper, bookRepository);
    }

    @Test
    void save_BookDtoWithEmptyGenres_ThrowsValidationException() {
        // Given
        BookDto invalidDto = createTestBookDto();
        invalidDto.setGenres(new HashSet<>());

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookService.save(invalidDto));

        assertTrue(exception.getErrors().containsKey("genres"));
        assertEquals("At least one genre must be specified", exception.getErrors().get("genres"));
        verifyNoInteractions(authorRepository, genreRepository, bookMapper, bookRepository);
    }

    @Test
    void save_BookDtoWithMultipleValidationErrors_ThrowsValidationException() {
        // Given
        BookDto invalidDto = new BookDto();
        // Все поля null или пустые

        // When/Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookService.save(invalidDto));

        Map<String, String> errors = exception.getErrors();
        assertEquals(3, errors.size());
        assertTrue(errors.containsKey("title"));
        assertTrue(errors.containsKey("author"));
        assertTrue(errors.containsKey("genres"));
        verifyNoInteractions(authorRepository, genreRepository, bookMapper, bookRepository);
    }

    private BookDto createTestBookDto() {
        BookDto dto = new BookDto();
        dto.setId(1L);
        dto.setTitle("Test Book");
        dto.setIsbn("Test-ISBN");
        dto.setPublicationYear(2024);
        // Добавляем автора
        AuthorShortDto authorDto = new AuthorShortDto();
        authorDto.setId(1L);
        dto.setAuthor(authorDto);
        // Добавляем хотя бы один жанр
        Set<GenreShortDto> genres = new HashSet<>();
        GenreShortDto genreDto = new GenreShortDto();
        genreDto.setId(1L);
        genres.add(genreDto);
        dto.setGenres(genres);
        return dto;
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("Test-ISBN");
        book.setPublicationYear(2024);
        // Добавляем автора
        Author author = createTestAuthor();
        book.setAuthor(author);
        // Добавляем хотя бы один жанр
        Set<Genre> genres = new HashSet<>();
        Genre genre = createTestGenre();
        genres.add(genre);
        book.setGenres(genres);
        return book;
    }

    private Author createTestAuthor() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Test Author");
        author.setLastName("Test Last Name");
        return author;
    }

    private Genre createTestGenre() {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Test Genre");
        genre.setDescription("Test Description");
        return genre;
    }
}
