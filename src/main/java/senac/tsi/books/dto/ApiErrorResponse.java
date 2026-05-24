package senac.tsi.books.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Resposta padrao de erro da API")
public class ApiErrorResponse {

    @Schema(description = "Data e hora do erro", example = "2026-05-24T18:30:00")
    private String timestamp;

    @Schema(description = "Codigo HTTP", example = "400")
    private int status;

    @Schema(description = "Titulo resumido do erro", example = "Erro de validacao")
    private String erro;

    @Schema(description = "Mensagem clara sobre o problema", example = "Um ou mais campos contem dados invalidos.")
    private String mensagem;

    @Schema(description = "Erros por campo, quando houver validacao de entrada")
    private Map<String, String> campos;

    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getErro() { return erro; }
    public String getMensagem() { return mensagem; }
    public Map<String, String> getCampos() { return campos; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setStatus(int status) { this.status = status; }
    public void setErro(String erro) { this.erro = erro; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public void setCampos(Map<String, String> campos) { this.campos = campos; }
}
