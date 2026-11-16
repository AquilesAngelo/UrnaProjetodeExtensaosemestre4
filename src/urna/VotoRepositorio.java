package urna;

import java.util.Map;

/** Contrato de persistência dos votos (Firebase ou modo local). */
public interface VotoRepositorio {

    String CANDIDATO = "CANDIDATO";
    String BRANCO = "BRANCO";
    String NULO = "NULO";

    /** Registra um voto de forma anônima. numero é nulo para branco/nulo. */
    void registrarVoto(String sessaoId, Cargo cargo, String tipo, String numero);

    /** Salva a apuração final da sessão. */
    void salvarApuracao(String sessaoId, Map<String, Object> resumo);

    /** Libera recursos e aguarda envios pendentes. */
    default void fechar() { }
}