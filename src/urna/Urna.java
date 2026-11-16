package urna;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Urna {
    /** Maior quantidade de dígitos entre os cargos (usada para montar a tela). */
    public static final int MAX_DIGITOS = 3;

    private final Map<Cargo, Map<String, Candidato>> candidatos = new EnumMap<>(Cargo.class);
    private final Map<Cargo, Integer> brancos = new EnumMap<>(Cargo.class);
    private final Map<Cargo, Integer> nulos = new EnumMap<>(Cargo.class);
    private final VotoRepositorio repositorio;
    private final String sessaoId = UUID.randomUUID().toString();

    public Urna(VotoRepositorio repositorio) {
        this.repositorio = repositorio;
        for (Cargo cargo : Cargo.values()) {
            candidatos.put(cargo, new LinkedHashMap<>());
            brancos.put(cargo, 0);
            nulos.put(cargo, 0);
        }
    }

    public void cadastrar(Candidato candidato) {
        candidatos.get(candidato.getCargo()).put(candidato.getNumero(), candidato);
    }

    public Candidato buscar(Cargo cargo, String numero) {
        return candidatos.get(cargo).get(numero);
    }

    public void votar(Candidato candidato) {
        candidato.adicionarVoto();
        repositorio.registrarVoto(sessaoId, candidato.getCargo(), VotoRepositorio.CANDIDATO, candidato.getNumero());
    }

    public void votarBranco(Cargo cargo) {
        brancos.merge(cargo, 1, Integer::sum);
        repositorio.registrarVoto(sessaoId, cargo, VotoRepositorio.BRANCO, null);
    }

    public void votarNulo(Cargo cargo) {
        nulos.merge(cargo, 1, Integer::sum);
        repositorio.registrarVoto(sessaoId, cargo, VotoRepositorio.NULO, null);
    }

    public int getBrancos(Cargo cargo) { return brancos.get(cargo); }

    public int getNulos(Cargo cargo) { return nulos.get(cargo); }

    public int totalVotos(Cargo cargo) {
        int total = brancos.get(cargo) + nulos.get(cargo);
        for (Candidato c : candidatos.get(cargo).values()) {
            total += c.getVotos();
        }
        return total;
    }

    public List<Candidato> ranking(Cargo cargo) {
        List<Candidato> lista = new ArrayList<>(candidatos.get(cargo).values());
        lista.sort(Comparator.comparingInt(Candidato::getVotos).reversed());
        return lista;
    }

    /** Todos os candidatos de todos os cargos (usado para carregar as fotos). */
    public List<Candidato> todosCandidatos() {
        List<Candidato> todos = new ArrayList<>();
        for (Map<String, Candidato> porCargo : candidatos.values()) {
            todos.addAll(porCargo.values());
        }
        return todos;
    }

    /** Envia a apuração final para a persistência. */
    public void encerrar() {
        Map<String, Object> resumo = new LinkedHashMap<>();
        resumo.put("sessao", sessaoId);
        for (Cargo cargo : Cargo.values()) {
            Map<String, Object> dadosCargo = new LinkedHashMap<>();
            Map<String, Object> votosPorCandidato = new LinkedHashMap<>();
            for (Candidato c : candidatos.get(cargo).values()) {
                votosPorCandidato.put(c.getNumero(), c.getVotos());
            }
            dadosCargo.put("candidatos", votosPorCandidato);
            dadosCargo.put("brancos", brancos.get(cargo));
            dadosCargo.put("nulos", nulos.get(cargo));
            dadosCargo.put("total", totalVotos(cargo));
            resumo.put(cargo.name(), dadosCargo);
        }
        repositorio.salvarApuracao(sessaoId, resumo);
    }
}