package urna;

/** Abstração de candidato. Cada cargo define sua própria especialização. */
public abstract class Candidato {
    private final String numero;
    private final String nome;
    private final String partido;
    private int votos;

    protected Candidato(String numero, String nome, String partido) {
        this.numero = numero;
        this.nome = nome;
        this.partido = partido;
    }

    public String getNumero() { return numero; }
    public String getNome() { return nome; }
    public String getPartido() { return partido; }
    public int getVotos() { return votos; }

    public void adicionarVoto() { votos++; }

    /** Cargo ao qual o candidato concorre (definido pelas subclasses). */
    public abstract Cargo getCargo();

    /** Texto exibido na tela da urna sob o nome (polimórfico). Pode usar HTML simples. */
    public abstract String getDescricaoTela();
}