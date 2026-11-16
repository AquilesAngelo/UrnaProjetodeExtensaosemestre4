package urna;

/** Cargos em disputa, na ordem em que o eleitor vota. */
public enum Cargo {
    VEREADOR("VEREADOR", 3, Tipo.PROPORCIONAL),
    PREFEITO("PREFEITO", 2, Tipo.MAJORITARIO);

    public enum Tipo { PROPORCIONAL, MAJORITARIO }

    private final String rotulo;
    private final int digitos;
    private final Tipo tipo;

    Cargo(String rotulo, int digitos, Tipo tipo) {
        this.rotulo = rotulo;
        this.digitos = digitos;
        this.tipo = tipo;
    }

    public String getRotulo() { return rotulo; }
    public int getDigitos() { return digitos; }
    public Tipo getTipo() { return tipo; }
}