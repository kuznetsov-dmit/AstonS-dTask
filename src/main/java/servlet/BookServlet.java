package servlet;

import dto.BookDto;
import exception.DatabaseException;
import exception.LibraryException;
import exception.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BookService;
import service.factory.ServiceFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookServlet extends BaseServlet {
    private final BookService bookService;

    public BookServlet() {
        this.bookService = ServiceFactory.getInstance().getBookService();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<BookDto> books = bookService.findAll();
                sendResponse(response, books, HttpServletResponse.SC_OK);
            } else {
                Long id = parseId(pathInfo);
                BookDto book = bookService.findById(id);
                sendResponse(response, book, HttpServletResponse.SC_OK);
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
            BookDto bookDto = readRequestBody(request, BookDto.class);
            validateBookDto(bookDto);
            BookDto savedBook = bookService.save(bookDto);
            sendResponse(response, savedBook, HttpServletResponse.SC_CREATED);
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
            BookDto bookDto = readRequestBody(request, BookDto.class);
            validateBookDto(bookDto);
            bookDto.setId(id);
            BookDto updatedBook = bookService.save(bookDto);
            sendResponse(response, updatedBook, HttpServletResponse.SC_OK);
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
            bookService.deleteById(id);
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

    private void validateBookDto(BookDto bookDto) {
        Map<String, String> errors = new HashMap<>();

        if (bookDto.getTitle() == null || bookDto.getTitle().trim().isEmpty()) {
            errors.put("title", "Book title cannot be empty");
        }

        if (bookDto.getAuthor() == null || bookDto.getAuthor().getId() == null) {
            errors.put("author", "Author must be specified");
        }

        if (bookDto.getGenres() == null || bookDto.getGenres().isEmpty()) {
            errors.put("genres", "At least one genre must be specified");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Book validation failed", errors);
        }
    }

    BookServlet(BookService bookService) {
        this.bookService = bookService;
    }

    public BookService getBookService() {
        return bookService;
    }
}