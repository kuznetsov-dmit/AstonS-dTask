package servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.AuthorShortDto;
import dto.BookDto;
import dto.GenreShortDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import service.BookService;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private BookService bookService;

    private BookServlet servlet;
    private StringWriter responseWriter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new BookServlet(bookService);
        responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(writer);
        objectMapper = new ObjectMapper();
    }

    @Test
    void doGet_WhenNoPathInfo_ReturnsAllBooks() throws IOException {
        // Given
        List<BookDto> books = Arrays.asList(createTestBookDto(), createTestBookDto());
        when(request.getPathInfo()).thenReturn(null);
        when(bookService.findAll()).thenReturn(books);

        // When
        servlet.doGet(request, response);

        // Then
        verify(bookService).findAll();
        verify(response).setStatus(HttpServletResponse.SC_OK);
        List<BookDto> result = objectMapper.readValue(responseWriter.toString(),
                new TypeReference<List<BookDto>>() {});
        assertEquals(2, result.size());
    }

    @Test
    void doGet_WithValidId_ReturnsBook() throws IOException {
        // Given
        Long bookId = 1L;
        BookDto book = createTestBookDto();
        when(request.getPathInfo()).thenReturn("/" + bookId);
        when(bookService.findById(bookId)).thenReturn(book);

        // When
        servlet.doGet(request, response);

        // Then
        verify(bookService).findById(bookId);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        BookDto result = objectMapper.readValue(responseWriter.toString(), BookDto.class);
        assertEquals(book.getId(), result.getId());
    }

    @Test
    void doPost_WithValidBook_ReturnsCreatedBook() throws IOException {
        // Given
        BookDto inputDto = createTestBookDto();
        String requestBody = objectMapper.writeValueAsString(inputDto);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);
        when(bookService.save(any(BookDto.class))).thenReturn(inputDto);

        // When
        servlet.doPost(request, response);

        // Then
        verify(bookService).save(any(BookDto.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        BookDto result = objectMapper.readValue(responseWriter.toString(), BookDto.class);
        assertEquals(inputDto.getId(), result.getId());
    }

    @Test
    void doPost_WithInvalidBook_ReturnsBadRequest() throws IOException {
        // Given
        BookDto invalidDto = new BookDto(); // Без обязательных полей
        String requestBody = objectMapper.writeValueAsString(invalidDto);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);

        // When
        servlet.doPost(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(bookService);
    }

    @Test
    void doPut_WithValidBook_ReturnsUpdatedBook() throws IOException {
        // Given
        Long bookId = 1L;
        BookDto inputDto = createTestBookDto();
        String requestBody = objectMapper.writeValueAsString(inputDto);

        when(request.getPathInfo()).thenReturn("/" + bookId);
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);
        when(bookService.save(any(BookDto.class))).thenReturn(inputDto);

        // When
        servlet.doPut(request, response);

        // Then
        verify(bookService).save(any(BookDto.class));
        verify(response).setStatus(HttpServletResponse.SC_OK);
        BookDto result = objectMapper.readValue(responseWriter.toString(), BookDto.class);
        assertEquals(inputDto.getId(), result.getId());
    }

    @Test
    void doDelete_WithValidId_ReturnsNoContent() throws IOException {
        // Given
        Long bookId = 1L;
        when(request.getPathInfo()).thenReturn("/" + bookId);
        doNothing().when(bookService).deleteById(bookId);

        // When
        servlet.doDelete(request, response);

        // Then
        verify(bookService).deleteById(bookId);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private BookDto createTestBookDto() {
        BookDto dto = new BookDto();
        dto.setId(1L);
        dto.setTitle("Test Book");
        dto.setIsbn("Test-ISBN");
        dto.setPublicationYear(2024);

        AuthorShortDto authorDto = new AuthorShortDto();
        authorDto.setId(1L);
        dto.setAuthor(authorDto);

        Set<GenreShortDto> genres = new HashSet<>();
        GenreShortDto genreDto = new GenreShortDto();
        genreDto.setId(1L);
        genres.add(genreDto);
        dto.setGenres(genres);

        return dto;
    }
}
