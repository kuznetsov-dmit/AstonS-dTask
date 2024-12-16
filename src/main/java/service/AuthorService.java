package service;

import dto.AuthorDto;
import entity.Author;
import exception.EntityNotFoundException;
import exception.ValidationException;
import mapper.AuthorMapper;
import repository.AuthorRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public AuthorDto findById(Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id, id.toString()));
    }

    public AuthorDto save(AuthorDto authorDto) {
        validateAuthorDto(authorDto);
        Author author = authorMapper.toEntity(authorDto);
        author = authorRepository.save(author);
        return authorMapper.toDto(author);
    }
    public List<AuthorDto> findAll() {
        return authorRepository.findAll()
                .stream().map(authorMapper::toDto)
                .collect(Collectors.toList());
    }
    public void deleteById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author", id.toString()));
        authorRepository.deleteById(id);
    }

    private void validateAuthorDto(AuthorDto authorDto) {
        Map<String, String> errors = new HashMap<>();

        if (authorDto.getFirstName() == null || authorDto.getFirstName().trim().isEmpty()) {
            errors.put("firstName", "First name cannot be empty");
        }

        if (authorDto.getLastName() == null || authorDto.getLastName().trim().isEmpty()) {
            errors.put("lastName", "Last name cannot be empty");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Author validation failed", errors);
        }
    }
}