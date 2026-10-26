package com.nttdata.cuentaflow.exception;

import com.nttdata.cuentaflow.dto.ErrorRespuesta;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

/** Traduce las excepciones a respuestas HTTP con ErrorRespuesta (RN-08). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Ejemplo resuelto: cuenta inexistente -> 404 CUENTA_NO_ENCONTRADA (RN-06). */
    @ExceptionHandler(CuentaNoEncontradaException.class)
    public ResponseEntity<ErrorRespuesta> cuentaNoEncontrada(CuentaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorRespuesta.of("CUENTA_NO_ENCONTRADA", ex.getMessage()));
    }

    // TODO 12 (RN-03): maneja CuentaDuplicadaException -> 409 CONFLICT con codigo "CUENTA_DUPLICADA".
    //  Copia el patron del ejemplo cuentaNoEncontrada de arriba.
    @ExceptionHandler(CuentaDuplicadaException.class)
    public ResponseEntity<ErrorRespuesta> cuentaDuplicada(CuentaDuplicadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorRespuesta.of("CUENTA_DUPLICADA", ex.getMessage()));
    }

    // TODO 13 (RN-04): maneja ReglaNegocioException -> 400 BAD_REQUEST usando ex.getCodigo() como codigo.
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorRespuesta> ReglaNegocio(ReglaNegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorRespuesta.of("SALDO_MINIMO", ex.getMessage()));
    }

    // TODO 14 (RN-01): maneja WebExchangeBindException (falla de @Valid) -> 400 con codigo "VALIDACION".
    //  El mensaje debe listar los campos con error: ex.getFieldErrors() -> "campo: mensaje; campo: mensaje".
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorRespuesta> webExchangeBind(WebExchangeBindException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorRespuesta.of("VALIDACION", ex.getMessage()));
    }
}
