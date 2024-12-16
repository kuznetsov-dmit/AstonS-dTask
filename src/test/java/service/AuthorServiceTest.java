package service;

import dto.AuthorDto;
import dto.BookShortDto;
import entity.Author;
import entity.Book;
import exception.EntityNotFoundException;
import exception.ValidationException;
import mapper.AuthorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.AuthorRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        authorService = new AuthorService(authorRepository, authorMapper);
    }

    @Test
    void findById_WhenAuthorExists_ReturnsAuthorDto() {
        // Given
        Long authorId = 1L;
        Author author = createTestAuthor();
        AuthorDto expectedDto = createTestAuthorDto();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorMapper.toDto(author)).thenReturn(expectedDto);

        // When
        AuthorDto result = authorService.findById(authorId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getFirstName(), result.getFirstName());
        assertEquals(expectedDto.getLastName(), result.getLastName());
        verify(authorRepository).findById(authorId);
        verify(authorMapper).toDto(author);
    }

    @Test
    void findById_WhenAuthorDoesNotExist_ThrowsException() {
        // Given
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> authorService.findById(authorId));
        verify(authorRepository).findById(authorId);
        verifyNoInteractions(authorMapper);
    }

    @Test
    void save_ValidAuthorDto_ReturnsSavedAuthorDto() {
        // Given
        AuthorDto inputDto = createTestAuthorDto();
        Author author = createTestAuthor();
        Author savedAuthor = createTestAuthor();
        AuthorDto expectedDto = createTestAuthorDto();

        when(authorMapper.toEntity(inputDto)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(savedAuthor);
        when(authorMapper.toDto(savedAuthor)).thenReturn(expectedDto);

        // When
        AuthorDto result = authorService.save(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getFirstName(), result.getFirstName());
        assertEquals(expectedDto.getLastName(), result.getLastName());
        verify(authorMapper).toEntity(inputDto);
        verify(authorRepository).save(author);
        verify(authorMapper).toDto(savedAuthor);
    }

    @Test
    void save_InvalidAuthorDto_ThrowsValidationException() {
        // Given
        AuthorDto invalidDto = new AuthorDto();

        // When/Then
        assertThrows(ValidationException.class, () -> authorService.save(invalidDto));
        verifyNoInteractions(authorRepository, authorMapper);
    }

    @Test
    void findAll_ReturnsAllAuthors() {
        // Given
        List<Author> authors = Arrays.asList(createTestAuthor(), createTestAuthor());
        List<AuthorDto> expectedDtos = Arrays.asList(createTestAuthorDto(), createTestAuthorDto());

        when(authorRepository.findAll()).thenReturn(authors);
        when(authorMapper.toDto(any(Author.class))).thenReturn(expectedDtos.get(0), expectedDtos.get(1));

        // When
        List<AuthorDto> result = authorService.findAll();

        // Then
        assertEquals(expectedDtos.size(), result.size());
        verify(authorRepository).findAll();
        verify(authorMapper, times(2)).toDto(any(Author.class));
    }

    @Test
    void deleteById_ExistingAuthor_DeletesSuccessfully() {
        // Given
        Long authorId = 1L;
        Author author = createTestAuthor();
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        doNothing().when(authorRepository).deleteById(authorId);

        // When
        authorService.deleteById(authorId);

        // Then
        verify(authorRepository).findById(authorId);
        verify(authorRepository).deleteById(authorId);
    }

    @Test
    void deleteById_NonExistingAuthor_ThrowsException() {
        // Given
        Long authorId = 999L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> authorService.deleteById(authorId));
        verify(authorRepository).findById(authorId);
        verify(authorRepository, never()).deleteById(any());
    }

    private Author createTestAuthor() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Test First Name");
        author.setLastName("Test Last Name");
        author.setBiography("Test Biography");
        author.setBooks(new HashSet<>());
        return author;
    }

    private AuthorDto createTestAuthorDto() {
        AuthorDto dto = new AuthorDto();
        dto.setId(1L);
        dto.setFirstName("Test First Name");
        dto.setLastName("Test Last Name");
        dto.setBiography("Test Biography");
        dto.setBooks(new HashSet<>());
        return dto;
    }
}
