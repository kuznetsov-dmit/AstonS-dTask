package servlet;

import dto.AuthorDto;
import exception.LibraryException;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AuthorService;
import service.factory.ServiceFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AuthorServlet extends BaseServlet {
    private final AuthorService authorService;

    public AuthorServlet() {
        this.authorService = ServiceFactory.getInstance().getAuthorService();
    }

    AuthorServlet(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                // Если путь /api/authors или /api/authors/, возвращаем всех авторов
                List<AuthorDto> authors = authorService.findAll();
                sendResponse(response, authors, HttpServletResponse.SC_OK);
            } else {
                // Если путь /api/authors/{id}, возвращаем конкретного автора
                Long id = parseId(pathInfo);
                AuthorDto author = authorService.findById(id);
                sendResponse(response, author, HttpServletResponse.SC_OK);
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
            AuthorDto authorDto = readRequestBody(request, AuthorDto.class);
            AuthorDto savedAuthor = authorService.save(authorDto);
            sendResponse(response, savedAuthor, HttpServletResponse.SC_CREATED);
        } catch (LibraryException e) {
            handleException(e, response);
        } catch (Exception e) {
            handleException(new LibraryException("Internal server error", e), response);
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long id = parseId(request.getPathInfo());
            authorService.deleteById(id);
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
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Long id = parseId(request.getPathInfo());
            AuthorDto authorDto = readRequestBody(request, AuthorDto.class);
            authorDto.setId(id);
            AuthorDto updatedAuthor = authorService.save(authorDto);
            sendResponse(response, updatedAuthor, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            handleException(new ValidationException("Invalid ID format",
                    Map.of("id", e.getMessage())), response);
        } catch (LibraryException e) {
            handleException(e, response);
        } catch (Exception e) {
            handleException(new LibraryException("Internal server error", e), response);
        }
    }



    public AuthorService getAuthorService() {
        return authorService;
    }
}
