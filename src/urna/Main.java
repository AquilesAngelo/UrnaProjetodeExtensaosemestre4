package urna;

import javax.swing.SwingUtilities;

public class Main {
    private static final String CHAVE_FIREBASE = "firebase-chave.json";

    public static void main(String[] args) {
        VotoRepositorio repositorio;
        try {
            repositorio = VotoRepositorioFirestore.conectar(CHAVE_FIREBASE);
            System.out.println("Conectado ao Firebase (Cloud Firestore).");
        } catch (Exception e) {
            System.err.println("Firebase indisponível (" + e.getMessage()
                    + "). Executando em modo local, sem persistência em nuvem.");
            repositorio = new VotoRepositorioLocal();
        }
        final VotoRepositorio repo = repositorio;
        Runtime.getRuntime().addShutdownHook(new Thread(repo::fechar));

        Urna urna = new Urna(repo);

        // Vereador (proporcional) - 3 dígitos
        urna.cadastrar(new CandidatoProporcional("745", "Adrian Mauricio", "Roxo"));
        urna.cadastrar(new CandidatoProporcional("890", "Paula Dino", "Laranja"));

        // Prefeito (majoritário) - 2 dígitos, com vice
        urna.cadastrar(new CandidatoMajoritario("19", "Bianca Souza", "Amarelo", "Caio Ramos"));
        urna.cadastrar(new CandidatoMajoritario("31", "Pedro Carreiro", "Verde", "Lia Matos"));
        urna.cadastrar(new CandidatoMajoritario("68", "Alberto Valente", "Azul", "Davi Leal"));

        SwingUtilities.invokeLater(() -> new UrnaFrame(urna).setVisible(true));
    }
}