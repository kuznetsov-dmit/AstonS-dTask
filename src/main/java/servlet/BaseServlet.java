package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.ErrorResponse;
import dto.ValidationErrorResponse;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseServlet extends HttpServlet {
    protected final ObjectMapper objectMapper;

    protected BaseServlet() {
        this.objectMapper = new ObjectMapper();
    }

    protected void sendResponse(HttpServletResponse response, Object data, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), data);
    }

    protected void sendError(HttpServletResponse response, LibraryException e, int status) throws IOException {
        ErrorResponse errorResponse;
        if (e instanceof ValidationException) {
            errorResponse = new ValidationErrorResponse(e.getMessage(),
                    ((ValidationException) e).getErrors());
        } else {
            errorResponse = new ErrorResponse(e.getMessage());
        }

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    protected <T> T readRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining());
        return objectMapper.readValue(body, clazz);
    }

    protected void handleException(LibraryException e, HttpServletResponse response) throws IOException {
        if (e instanceof EntityNotFoundException) {
            sendError(response, e, HttpServletResponse.SC_NOT_FOUND);
        } else if (e instanceof ValidationException || e instanceof DuplicateEntityException) {
            sendError(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } else if (e instanceof DatabaseException) {
            sendError(response, new LibraryException("Internal server error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else {
            sendError(response, new LibraryException("Internal server error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected Long parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new ValidationException("Path must contain an ID",
                    Map.of("id", "Path parameter is missing"));
        }

        String idStr = pathInfo.substring(1); // удаляем начальный слэш
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid ID format",
                    Map.of("id", "ID must be a number but was: '" + idStr + "'"));
        }
    }
}