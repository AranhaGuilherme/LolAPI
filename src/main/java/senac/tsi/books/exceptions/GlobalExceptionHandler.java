package senac.tsi.books.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
        Map<String, Object> erro = new HashMap<>();
        erro.put("status", 404);
        erro.put("erro", "Recurso não encontrado");
        erro.put("mensagem", ex.getMessage());
        erro.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, Object> erro = new HashMap<>();
        Map<String, String> campos = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            campos.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        erro.put("status", 400);
        erro.put("erro", "Erro de validação");
        erro.put("campos", campos);
        erro.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonInvalido(HttpMessageNotReadableException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("status", 400);
        erro.put("erro", "Requisição inválida");
        erro.put("mensagem", "JSON inválido ou valor não permitido. Verifique os campos enviados (ex: role deve ser TOP, JUNGLE, MID, ADC ou SUPPORT).");
        erro.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("status", 400);
        erro.put("erro", "Parâmetro inválido");
        erro.put("mensagem", "O valor '" + ex.getValue() + "' não é válido para o parâmetro '" + ex.getName() + "'. Esperado: número inteiro.");
        erro.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleParametroAusente(MissingServletRequestParameterException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("status", 400);
        erro.put("erro", "Parâmetro obrigatório ausente");
        erro.put("mensagem", "O parâmetro '" + ex.getParameterName() + "' é obrigatório e não foi informado.");
        erro.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegridade(DataIntegrityViolationException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("status", 400);
        erro.put("erro", "Violação de integridade");
        erro.put("mensagem", "Operação inválida: verifique se os IDs de entidades relacionadas existem e se não há duplicatas.");
        erro.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEndpointNaoEncontrado(NoResourceFoundException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("status", 404);
        erro.put("erro", "Endpoint não encontrado");
        erro.put("mensagem", "O caminho '" + ex.getResourcePath() + "' não existe nesta API.");
        erro.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleErroGenerico(Exception ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("status", 500);
        erro.put("erro", "Erro interno do servidor");
        erro.put("mensagem", "Ocorreu um erro inesperado. Tente novamente mais tarde.");
        erro.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
