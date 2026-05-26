package senac.tsi.books.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return erro(HttpStatus.NOT_FOUND, "Recurso nao encontrado", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            campos.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> erro = corpo(HttpStatus.BAD_REQUEST, "Erro de validacao", "Um ou mais campos contem dados invalidos.");
        erro.put("campos", campos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> campos = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            campos.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        Map<String, Object> erro = corpo(HttpStatus.BAD_REQUEST, "Erro de validacao", "Um ou mais campos contem dados invalidos.");
        erro.put("campos", campos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleRegraDeNegocio(IllegalArgumentException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Regra de negocio invalida", ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return erro(
                HttpStatus.BAD_REQUEST,
                "Requisicao invalida",
                "JSON invalido ou valor nao permitido. Verifique tipos, enums e campos enviados."
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return erro(
                HttpStatus.BAD_REQUEST,
                "Parametro invalido",
                "O valor '" + ex.getValue() + "' nao e valido para o parametro '" + ex.getName() + "'."
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleParametroAusente(MissingServletRequestParameterException ex) {
        return erro(
                HttpStatus.BAD_REQUEST,
                "Parametro obrigatorio ausente",
                "O parametro '" + ex.getParameterName() + "' e obrigatorio e nao foi informado."
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegridade(DataIntegrityViolationException ex) {
        return erro(
                HttpStatus.CONFLICT,
                "Violacao de integridade",
                "Operacao invalida: verifique IDs relacionados, duplicidades ou registros vinculados."
        );
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<Map<String, Object>> handleReferenciaNaoEncontrada(JpaObjectRetrievalFailureException ex) {
        return erro(
                HttpStatus.NOT_FOUND,
                "Recurso relacionado nao encontrado",
                "Um dos IDs relacionados informados nao existe."
        );
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<Map<String, Object>> handleUsoInvalidoDeDados(InvalidDataAccessApiUsageException ex) {
        return erro(
                HttpStatus.BAD_REQUEST,
                "Requisicao invalida",
                "Verifique os IDs e relacionamentos enviados no corpo da requisicao."
        );
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleConflitoDeEstado(ObjectOptimisticLockingFailureException ex) {
        return erro(
                HttpStatus.CONFLICT,
                "Conflito de estado",
                "Nao informe ID manualmente em POST. Use 0 ou omita o campo para o banco gerar automaticamente."
        );
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> handleErroDeTransacao(TransactionSystemException ex) {
        return erro(
                HttpStatus.BAD_REQUEST,
                "Requisicao invalida",
                "A operacao nao pode ser concluida com os dados enviados. Verifique campos obrigatorios e relacionamentos."
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEndpointNaoEncontrado(NoResourceFoundException ex) {
        return erro(HttpStatus.NOT_FOUND, "Endpoint nao encontrado", "O caminho '" + ex.getResourcePath() + "' nao existe nesta API.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleErroGenerico(Exception ex) {
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    private ResponseEntity<Map<String, Object>> erro(HttpStatus status, String titulo, String mensagem) {
        return ResponseEntity.status(status).body(corpo(status, titulo, mensagem));
    }

    private Map<String, Object> corpo(HttpStatus status, String titulo, String mensagem) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("status", status.value());
        erro.put("erro", titulo);
        erro.put("mensagem", mensagem);
        erro.put("timestamp", LocalDateTime.now().toString());
        return erro;
    }
}
