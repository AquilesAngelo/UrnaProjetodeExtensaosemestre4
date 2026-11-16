package urna;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Persistência no Cloud Firestore.
 * Coleções: "votos" (um documento por voto, anônimo) e "apuracoes" (um documento por sessão).
 * Os envios rodam em outra thread para não travar a interface.
 */
public class VotoRepositorioFirestore implements VotoRepositorio {

    private final Firestore db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private VotoRepositorioFirestore(Firestore db) {
        this.db = db;
    }

    public static VotoRepositorioFirestore conectar(String caminhoChave) throws IOException {
        try (FileInputStream chave = new FileInputStream(caminhoChave)) {
            FirebaseOptions opcoes = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(chave))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(opcoes);
            }
        }
        return new VotoRepositorioFirestore(FirestoreClient.getFirestore());
    }

    @Override
    public void registrarVoto(String sessaoId, Cargo cargo, String tipo, String numero) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("sessao", sessaoId);
        dados.put("cargo", cargo.name());
        dados.put("tipo", tipo);
        dados.put("numero", numero);
        dados.put("data", FieldValue.serverTimestamp());
        executor.submit(() -> {
            try {
                db.collection("votos").add(dados).get();
            } catch (Exception e) {
                System.err.println("Falha ao salvar voto no Firestore: " + e.getMessage());
            }
        });
    }

    @Override
    public void salvarApuracao(String sessaoId, Map<String, Object> resumo) {
        Map<String, Object> dados = new HashMap<>(resumo);
        dados.put("encerradaEm", FieldValue.serverTimestamp());
        executor.submit(() -> {
            try {
                db.collection("apuracoes").document(sessaoId).set(dados).get();
            } catch (Exception e) {
                System.err.println("Falha ao salvar apuração no Firestore: " + e.getMessage());
            }
        });
    }

    @Override
    public void fechar() {
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
