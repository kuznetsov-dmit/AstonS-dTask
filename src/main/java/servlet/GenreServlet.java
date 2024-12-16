package servlet;

import dto.GenreDto;
import exception.LibraryException;
import exception.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.GenreService;
import service.factory.ServiceFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenreServlet extends BaseServlet {
    private final GenreService genreService;

    public GenreServlet() {
        this.genreService = ServiceFactory.getInstance().getGenreService();
    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<GenreDto> genres = genreService.findAll();
                sendResponse(response, genres, HttpServletResponse.SC_OK);
            } else {
                Long id = parseId(pathInfo);
                GenreDto genre = genreService.findById(id);
                sendResponse(response, genre, HttpServletResponse.SC_OK);
            }
        } catch (IllegalArgumentException e) {
            handleException(new ValidationException("Invalid ID format",
                    Map.of("id", e.getMessage())), response);
        } catch (LibraryException e) {
            handleException(e, response);
        } catch (Exception e) {
            handleException(new LibraryException("Internal server error", e), response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            GenreDto genreDto = readRequestBody(request, GenreDto.class);
            validateGenreDto(genreDto);
            GenreDto savedGenre = genreService.save(genreDto);
            sendResponse(response, savedGenre, HttpServletResponse.SC_CREATED);
        } catch (LibraryException e) {
            handleException(e, response);
        } catch (Exception e) {
            handleException(new LibraryException("Internal server error", e), response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Long id = parseId(request.getPathInfo());
            GenreDto genreDto = readRequestBody(request, GenreDto.class);
            validateGenreDto(genreDto);
            genreDto.setId(id);
            GenreDto updatedGenre = genreService.save(genreDto);
            sendResponse(response, updatedGenre, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            handleException(new ValidationException("Invalid ID format",
                    Map.of("id", e.getMessage())), response);
        } catch (LibraryException e) {
            handleException(e, response);
        } catch (Exception e) {
            handleException(new LibraryException("Internal server error", e), response);
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Long id = parseId(request.getPathInfo());
            genreService.deleteById(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalArgumentException e) {
            handleException(new ValidationException("Invalid ID format",
                    Map.of("id", e.getMessage())), response);
        } catch (LibraryException e) {
            handleException(e, response);
        } catch (Exception e) {
            handleException(new LibraryException("Internal server error", e), response);
        }
    }

    private void validateGenreDto(GenreDto genreDto) {
        Map<String, String> errors = new HashMap<>();

        if (genreDto.getName() == null || genreDto.getName().trim().isEmpty()) {
            errors.put("name", "Genre name cannot be empty");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Genre validation failed", errors);
        }
    }

    GenreServlet(GenreService genreService) {
        this.genreService = genreService;
    }

    public GenreService getGenreService() {
        return genreService;
    }
}