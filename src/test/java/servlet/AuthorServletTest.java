package servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.AuthorDto;
import dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import service.AuthorService;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthorServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthorService authorService;

    private AuthorServlet servlet;
    private StringWriter responseWriter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        servlet = new AuthorServlet(authorService);
        responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(writer);
        objectMapper = new ObjectMapper();
    }

    @Test
    void doGet_WhenNoPathInfo_ReturnsAllAuthors() throws IOException {
        // Given
        List<AuthorDto> authors = Arrays.asList(createTestAuthorDto(), createTestAuthorDto());
        when(request.getPathInfo()).thenReturn(null);
        when(authorService.findAll()).thenReturn(authors);

        // When
        servlet.doGet(request, response);

        // Then
        verify(authorService).findAll();
        verify(response).setStatus(HttpServletResponse.SC_OK);
        List<AuthorDto> result = objectMapper.readValue(responseWriter.toString(),
                new TypeReference<List<AuthorDto>>() {});
        assertEquals(2, result.size());
    }

    @Test
    void doGet_WithValidId_ReturnsAuthor() throws IOException {
        // Given
        Long authorId = 1L;
        AuthorDto author = createTestAuthorDto();
        when(request.getPathInfo()).thenReturn("/" + authorId);
        when(authorService.findById(authorId)).thenReturn(author);

        // When
        servlet.doGet(request, response);

        // Then
        verify(authorService).findById(authorId);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        AuthorDto result = objectMapper.readValue(responseWriter.toString(), AuthorDto.class);
        assertEquals(author.getId(), result.getId());
    }

    @Test
    void doPost_WithValidAuthor_ReturnsCreatedAuthor() throws IOException {
        // Given
        AuthorDto inputDto = createTestAuthorDto();
        String requestBody = objectMapper.writeValueAsString(inputDto);

        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);
        when(authorService.save(any(AuthorDto.class))).thenReturn(inputDto);

        // When
        servlet.doPost(request, response);

        // Then
        verify(authorService).save(any(AuthorDto.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        AuthorDto result = objectMapper.readValue(responseWriter.toString(), AuthorDto.class);
        assertEquals(inputDto.getId(), result.getId());
    }

    @Test
    void doPut_WithValidAuthor_ReturnsUpdatedAuthor() throws IOException {
        // Given
        Long authorId = 1L;
        AuthorDto inputDto = createTestAuthorDto();
        String requestBody = objectMapper.writeValueAsString(inputDto);

        when(request.getPathInfo()).thenReturn("/" + authorId);
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(request.getReader()).thenReturn(reader);
        when(authorService.save(any(AuthorDto.class))).thenReturn(inputDto);

        // When
        servlet.doPut(request, response);

        // Then
        verify(authorService).save(any(AuthorDto.class));
        verify(response).setStatus(HttpServletResponse.SC_OK);
        AuthorDto result = objectMapper.readValue(responseWriter.toString(), AuthorDto.class);
        assertEquals(inputDto.getId(), result.getId());
    }

    @Test
    void doDelete_WithValidId_ReturnsNoContent() throws IOException, ServletException {
        // Given
        Long authorId = 1L;
        when(request.getPathInfo()).thenReturn("/" + authorId);
        doNothing().when(authorService).deleteById(authorId);

        // When
        servlet.doDelete(request, response);

        // Then
        verify(authorService).deleteById(authorId);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doGet_WithInvalidId_ReturnsBadRequest() throws IOException {
        // Given
        when(request.getPathInfo()).thenReturn("/invalid");

        // When
        servlet.doGet(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ErrorResponse error = objectMapper.readValue(responseWriter.toString(), ErrorResponse.class);
        assertNotNull(error.getMessage());
    }

    private AuthorDto createTestAuthorDto() {
        AuthorDto dto = new AuthorDto();
        dto.setId(1L);
        dto.setFirstName("Test");
        dto.setLastName("Author");
        dto.setBiography("Test Biography");
        return dto;
    }
}
