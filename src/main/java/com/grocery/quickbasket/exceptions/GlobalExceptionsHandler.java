package com.grocery.quickbasket.exceptions;

import com.grocery.quickbasket.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.UnknownHostException;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionsHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<Response<String>> handleUsernameNotFoundException(UsernameNotFoundException ex){
        return Response.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(UserAddressAlreadyExistException.class)
    public final ResponseEntity<Response<String>> handleUserAddressAlreadyExistException(UserAddressAlreadyExistException ex){
        return Response.failedResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(VoucherApplicationException.class)
    public final ResponseEntity<Response<String>> handleVoucherApplicationException(VoucherApplicationException ex){
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(PendingOrderExcerption.class)
    public final ResponseEntity<Response<String>> handlePendingOrderExcerption(PendingOrderExcerption ex){
        return Response.failedResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(StoreNameSameException.class)
    public final ResponseEntity<Response<String>> handleStoreNameSameException(StoreNameSameException ex){
        return Response.failedResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(StoreNotFoundException.class)
    public final ResponseEntity<Response<String>> handleStoreNotFoundException(StoreNotFoundException ex){
        return Response.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public final ResponseEntity<Response<String>> handleUserIdNotFoundException(UserIdNotFoundException ex){
        return Response.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(EmailNotExistException.class)
    public final ResponseEntity<?> handleEmailNotExistException(EmailNotExistException ex){
        return Response.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public final ResponseEntity<?> handlePasswordNotMatch(PasswordNotMatchException ex){
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public final ResponseEntity<?> handleEmailAlreadyExistException(EmailAlreadyExistException ex){
        return Response.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Response<String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        log.error(ex.getMessage(), ex);
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Unable to process the request", errorMessage);
    }


    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Response<String>> handleAllExceptions(Exception ex) {

        log.error(ex.getMessage(), ex.getCause(), ex);

        if (ex.getCause() instanceof UnknownHostException) {
            return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Unable to process the unknownhostexception", ex.getLocalizedMessage());
        }

        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Unable to process " + ex.getMessage(), ex.getClass().getName());
    }

    @ExceptionHandler(DataNotFoundException.class) 
    public final ResponseEntity<Response<String>> handleDataNotFoundException(DataNotFoundException ex) {
        return Response.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
    }
}
