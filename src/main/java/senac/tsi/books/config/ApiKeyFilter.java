package senac.tsi.books.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import senac.tsi.books.entities.ApiKey;
import senac.tsi.books.entities.ApiKeyRole;
import senac.tsi.books.repositories.ApiKeyRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Order(1)
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-API-Key";
    public static final String API_KEY_ROLE_ATTRIBUTE = "apiKeyRole";
    public static final String API_KEY_VALUE_ATTRIBUTE = "apiKeyValue";

    private final ApiKeyRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiKeyFilter(ApiKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || path.equals("/")
                || path.equals("/error")
                || path.startsWith("/api-keys/generate")
                || path.startsWith("/api/v1/api-keys/generate")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKeyValue = request.getHeader(API_KEY_HEADER);

        if (apiKeyValue == null || apiKeyValue.isBlank()) {
            escreverErro(response, HttpServletResponse.SC_UNAUTHORIZED, "API Key obrigatoria", "Informe o header X-API-Key.");
            return;
        }

        ApiKey apiKey = repository.findByKeyValueAndActiveTrue(apiKeyValue)
                .orElse(null);

        if (apiKey == null) {
            escreverErro(response, HttpServletResponse.SC_UNAUTHORIZED, "API Key invalida", "A chave enviada nao existe ou esta inativa.");
            return;
        }

        if ("DELETE".equalsIgnoreCase(request.getMethod()) && apiKey.getRole() != ApiKeyRole.ADMIN) {
            escreverErro(response, HttpServletResponse.SC_FORBIDDEN, "Acesso negado", "Operacoes DELETE exigem API Key com role ADMIN.");
            return;
        }

        request.setAttribute(API_KEY_ROLE_ATTRIBUTE, apiKey.getRole());
        request.setAttribute(API_KEY_VALUE_ATTRIBUTE, apiKey.getKeyValue());
        filterChain.doFilter(request, response);
    }

    private void escreverErro(HttpServletResponse response, int status, String erro, String mensagem) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status,
                "erro", erro,
                "mensagem", mensagem
        ));
    }
}
