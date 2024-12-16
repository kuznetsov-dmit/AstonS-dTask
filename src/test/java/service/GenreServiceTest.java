package service;

import dto.GenreDto;
import entity.Genre;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import mapper.GenreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GenreRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    private GenreService genreService;

    @BeforeEach
    void setUp() {
        genreService = new GenreService(genreRepository, genreMapper);
    }

    @Test
    void findById_WhenGenreExists_ReturnsGenreDto() {
        // Given
        Long genreId = 1L;
        Genre genre = createTestGenre();
        GenreDto expectedDto = createTestGenreDto();

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(genreMapper.toDto(genre)).thenReturn(expectedDto);

        // When
        GenreDto result = genreService.findById(genreId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        verify(genreRepository).findById(genreId);
        verify(genreMapper).toDto(genre);
    }

    @Test
    void findById_WhenGenreDoesNotExist_ThrowsException() {
        // Given
        Long genreId = 1L;
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> genreService.findById(genreId));
        verify(genreRepository).findById(genreId);
        verifyNoInteractions(genreMapper);
    }

    @Test
    void save_ValidGenreDto_ReturnsSavedGenreDto() {
        // Given
        GenreDto inputDto = createTestGenreDto();
        Genre genre = createTestGenre();
        Genre savedGenre = createTestGenre();
        GenreDto expectedDto = createTestGenreDto();

        when(genreMapper.toEntity(inputDto)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(savedGenre);
        when(genreMapper.toDto(savedGenre)).thenReturn(expectedDto);

        // When
        GenreDto result = genreService.save(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        verify(genreMapper).toEntity(inputDto);
        verify(genreRepository).save(genre);
        verify(genreMapper).toDto(savedGenre);
    }

    @Test
    void findAll_ReturnsAllGenres() {
        // Given
        List<Genre> genres = Arrays.asList(createTestGenre(), createTestGenre());
        List<GenreDto> expectedDtos = Arrays.asList(createTestGenreDto(), createTestGenreDto());

        when(genreRepository.findAll()).thenReturn(genres);
        when(genreMapper.toDto(any(Genre.class))).thenReturn(expectedDtos.get(0), expectedDtos.get(1));

        // When
        List<GenreDto> result = genreService.findAll();

        // Then
        assertEquals(expectedDtos.size(), result.size());
        verify(genreRepository).findAll();
        verify(genreMapper, times(2)).toDto(any(Genre.class));
    }

    @Test
    void deleteById_ExistingGenre_DeletesSuccessfully() {
        // Given
        Long genreId = 1L;
        Genre genre = createTestGenre();
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

        // When
        genreService.deleteById(genreId);

        // Then
        verify(genreRepository).findById(genreId);
        verify(genreRepository).deleteById(genreId);
    }

    @Test
    void deleteById_NonExistingGenre_ThrowsException() {
        // Given
        Long genreId = 999L;
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> genreService.deleteById(genreId));
        verify(genreRepository).findById(genreId);
        verify(genreRepository, never()).deleteById(any());
    }

    private Genre createTestGenre() {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Test Genre");
        genre.setDescription("Test Description");
        genre.setBooks(new HashSet<>());
        return genre;
    }

    private GenreDto createTestGenreDto() {
        GenreDto dto = new GenreDto();
        dto.setId(1L);
        dto.setName("Test Genre");
        dto.setDescription("Test Description");
        dto.setBooks(new HashSet<>());
        return dto;
    }
}
