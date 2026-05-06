package aiss_L3.VideoMiner.exception;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler({
      ChannelNotFoundException.class,
      VideoNotFoundException.class,
      CaptionNotFoundException.class,
      CommentNotFoundException.class
  })
  public ProblemDetail handleNotFoundExceptions(Exception ex) {
    log.warn("Resource not found: {}", ex.getMessage());
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setTitle("Resource Not Found");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    List<String> errors = fieldErrors.stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.toList());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "Validation failed for the request.");
    problemDetail.setTitle("Validation Error");
    problemDetail.setProperty("errors", errors);
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    log.warn("Malformed request body: {}", ex.getMessage());
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "Malformed request body.");
    problemDetail.setTitle("Bad Request");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    log.warn("Method not supported: {}", ex.getMessage());
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED,
        "HTTP method not supported for this endpoint.");
    problemDetail.setTitle("Method Not Allowed");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ProblemDetail handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
    log.warn("Media type not supported: {}", ex.getMessage());
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
        "Content-Type not supported.");
    problemDetail.setTitle("Unsupported Media Type");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ProblemDetail handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
    log.warn("Media type not acceptable: {}", ex.getMessage());
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_ACCEPTABLE,
        "Requested media type is not acceptable.");
    problemDetail.setTitle("Not Acceptable");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex) {
    log.error("Unexpected error occurred: ", ex);
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred processing your request.");
    problemDetail.setTitle("Internal Server Error");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }
}