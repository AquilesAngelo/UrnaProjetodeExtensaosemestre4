package urna;

/** Candidato a cargo majoritário (ex.: prefeito), que concorre com vice. */
public class CandidatoMajoritario extends Candidato {
    private final String vice;

    public CandidatoMajoritario(String numero, String nome, String partido, String vice) {
        super(numero, nome, partido);
        this.vice = vice;
    }

    public String getVice() { return vice; }

    @Override
    public Cargo getCargo() {
        return Cargo.PREFEITO;
    }

    @Override
    public String getDescricaoTela() {
        return "<html>Partido: " + getPartido() + "<br>Vice: " + vice + "</html>";
    }
}