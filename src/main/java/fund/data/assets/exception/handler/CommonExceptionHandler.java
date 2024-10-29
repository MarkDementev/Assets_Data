package fund.data.assets.exception.handler;

import fund.data.assets.config.LoggerConfig;
import fund.data.assets.exception.EntityWithIDNotFoundException;

import org.springdoc.api.ErrorMessage;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

/**
 * Класс с хэндлерами для всех исключений - и для проектных, и для дефолтных в Java.
 * @version 0.4-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
@ControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityWithIDNotFoundException.class)
    public ResponseEntity<ErrorMessage> entityWithIDNotFoundExceptionHandler(EntityWithIDNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
    }

    /**
     * Все реализованные исключения в проекте, кроме EntityWithIDNotFoundException, унаследованы от
     * IllegalArgumentException. Это исключение, а также прописанные в проекте наследники, должны сообщать юзеру текст
     * ошибки и выдавать HttpStatus.BAD_REQUEST с кодом 400.
     * @param e исключение либо класса IllegalArgumentException, либо его наследник.
     * @return ResponseEntity с текстом ошибки.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
        String errorMessage = e.getMessage();
        int errorIndexOf = errorMessage.indexOf("[ERROR:");
        int pointIndexOf = errorMessage.indexOf(".]");
        errorMessage = errorMessage.substring(errorIndexOf, pointIndexOf);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(errorMessage));
    }

    /**
     * Все не предусмотренные в бизнес-логике исключения обрабатываются здесь. Хэндлер позволяет не только показать
     * сообщение о нём, но и записывает информацию в лог для дальнейшей доработки проекта.
     * @param e исключение, не предусмотренное в бизнес-логике.
     * @return ResponseEntity с текстом ошибки.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> exceptionHandler(Exception e) {
        String errorMessage = Arrays.stream(e.getStackTrace()).findFirst() + " / " + e.getClass().getName()
                + " / " + e.getMessage();

        LoggerConfig.getLogger().error("Unexpected exception was thrown: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ErrorMessage(errorMessage));
    }
}
