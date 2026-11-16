package urna;

/** Candidato a cargo proporcional (ex.: vereador). */
public class CandidatoProporcional extends Candidato {

    public CandidatoProporcional(String numero, String nome, String partido) {
        super(numero, nome, partido);
    }

    @Override
    public Cargo getCargo() {
        return Cargo.VEREADOR;
    }

    @Override
    public String getDescricaoTela() {
        return "Partido: " + getPartido();
    }
}