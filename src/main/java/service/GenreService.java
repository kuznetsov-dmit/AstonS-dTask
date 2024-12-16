package service;

import dto.GenreDto;
import entity.Genre;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import mapper.GenreMapper;
import repository.GenreRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    public GenreDto findById(Long id) {
        return genreRepository.findById(id)
                .map(genreMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + id, id.toString()));
    }

    public GenreDto save(GenreDto genreDto) throws DuplicateEntityException {
        validateGenreDto(genreDto);

        if (genreDto.getId() == null &&
            genreRepository.findByName(genreDto.getName()).isPresent()) {
            throw new DuplicateEntityException("Genre", "name", genreDto.getName());
        }

        Genre genre = genreMapper.toEntity(genreDto);
        genre = genreRepository.save(genre);
        return genreMapper.toDto(genre);
    }

    private void validateGenreDto(GenreDto genreDto) {
        Map<String, String> errors = new HashMap<>();

        if (genreDto.getName() == null || genreDto.getName().trim().isEmpty()) {
            errors.put("name", "Name cannot be empty");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Genre validation failed", errors);
        }
    }
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }
    public void deleteById(Long id) throws EntityNotFoundException {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre", id.toString()));
        genreRepository.deleteById(id);
    }
}
