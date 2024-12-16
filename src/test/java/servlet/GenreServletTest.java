package servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ErrorResponse;
import dto.GenreDto;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import service.GenreService;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private GenreService genreService;

    private GenreServlet servlet;
    private StringWriter responseWriter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new GenreServlet(genreService);
        responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(writer);
        objectMapper = new ObjectMapper();
    }

    @Test
    void doGet_WithValidId_ReturnsGenre() throws IOException {
        // Given
        Long genreId = 1L;
        GenreDto genre = createTestGenreDto();
        when(request.getPathInfo()).thenReturn("/" + genreId);
        when(genreService.findById(genreId)).thenReturn(genre);

        // When
        servlet.doGet(request, response);

        // Then
        verify(genreService).findById(genreId);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        GenreDto result = objectMapper.readValue(responseWriter.toString(), GenreDto.class);
        assertEquals(genre.getId(), result.getId());
        assertEquals(genre.getName(), result.getName());
    }

    @Test
    void doPost_WithValidGenre_ReturnsCreatedGenre() throws IOException {
        // Given
        GenreDto inputDto = createTestGenreDto();
        String requestBody = objectMapper.writeValueAsString(inputDto);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);
        when(genreService.save(any(GenreDto.class))).thenReturn(inputDto);

        // When
        servlet.doPost(request, response);

        // Then
        verify(genreService).save(any(GenreDto.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        GenreDto result = objectMapper.readValue(responseWriter.toString(), GenreDto.class);
        assertEquals(inputDto.getId(), result.getId());
        assertEquals(inputDto.getName(), result.getName());
    }

    @Test
    void doPost_WithInvalidGenre_ReturnsBadRequest() throws IOException {
        // Given
        GenreDto invalidDto = new GenreDto(); // Без имени
        String requestBody = objectMapper.writeValueAsString(invalidDto);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);

        // When
        servlet.doPost(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(genreService);
    }

    @Test
    void doPut_WithValidGenre_ReturnsUpdatedGenre() throws IOException {
        // Given
        Long genreId = 1L;
        GenreDto inputDto = createTestGenreDto();
        String requestBody = objectMapper.writeValueAsString(inputDto);

        when(request.getPathInfo()).thenReturn("/" + genreId);
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);
        when(genreService.save(any(GenreDto.class))).thenReturn(inputDto);

        // When
        servlet.doPut(request, response);

        // Then
        verify(genreService).save(any(GenreDto.class));
        verify(response).setStatus(HttpServletResponse.SC_OK);
        GenreDto result = objectMapper.readValue(responseWriter.toString(), GenreDto.class);
        assertEquals(inputDto.getId(), result.getId());
        assertEquals(inputDto.getName(), result.getName());
    }

    @Test
    void doGet_WithInvalidId_ReturnsBadRequest() throws IOException {
        // Given
        StringWriter responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);

        when(response.getWriter()).thenReturn(writer);
        when(request.getPathInfo()).thenReturn("/invalid");

        // When
        servlet.doGet(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ErrorResponse error = objectMapper.readValue(responseWriter.toString(), ErrorResponse.class);
        assertNotNull(error.getMessage());
    }

    private GenreDto createTestGenreDto() {
        GenreDto dto = new GenreDto();
        dto.setId(1L);
        dto.setName("Test Genre");
        dto.setDescription("Test Description");
        dto.setBooks(new HashSet<>());
        return dto;
    }

    @Test
    void doDelete_WithValidId_ReturnsNoContent() throws IOException {
        // Given
        Long genreId = 1L;
        when(request.getPathInfo()).thenReturn("/" + genreId);
        doNothing().when(genreService).deleteById(genreId);

        // When
        servlet.doDelete(request, response);

        // Then
        verify(genreService).deleteById(genreId);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doGet_WhenNoPathInfo_ReturnsAllGenres() throws IOException {
        // Given
        List<GenreDto> genres = Arrays.asList(createTestGenreDto(), createTestGenreDto());
        StringWriter responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);

        when(response.getWriter()).thenReturn(writer);
        when(request.getPathInfo()).thenReturn(null);
        when(genreService.findAll()).thenReturn(genres);

        // When
        servlet.doGet(request, response);

        // Then
        verify(genreService).findAll();
        verify(response).setStatus(HttpServletResponse.SC_OK);
        List<GenreDto> result = objectMapper.readValue(responseWriter.toString(),
                new TypeReference<List<GenreDto>>() {});
        assertEquals(2, result.size());
    }
}