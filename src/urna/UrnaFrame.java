package urna;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UrnaFrame extends JFrame {

    private static final String SENHA_MESARIO = "1234";
    private static final String PASTA_FOTOS = "fotos";
    private static final String[] EXTENSOES = {"jpg", "jpeg", "png"};
    private static final int FOTO_LARGURA = 150;
    private static final int FOTO_ALTURA = 190;

    private static final String FAMILIA = "Segoe UI";
    private static final Color CORPO_TOPO = new Color(58, 64, 78);
    private static final Color CORPO_BASE = new Color(30, 34, 43);
    private static final Color TELA_TOPO = new Color(250, 251, 253);
    private static final Color TELA_BASE = new Color(228, 233, 241);
    private static final Color AZUL = new Color(28, 94, 186);
    private static final Color AZUL_ESCURO = new Color(18, 60, 128);
    private static final Color VERMELHO = new Color(200, 40, 50);
    private static final Color VERDE = new Color(30, 150, 80);
    private static final Color TEXTO = new Color(28, 34, 46);
    private static final Color TEXTO_SUAVE = new Color(105, 115, 132);
    private static final Color LINHA = new Color(205, 211, 222);

    private final Urna urna;
    private final Map<String, ImageIcon> fotos = new HashMap<>();
    private final StringBuilder digitos = new StringBuilder();
    private final Cargo[] cargos = Cargo.values();
    private int cargoIndex;
    private boolean branco;
    private boolean finalizado;
    private boolean encerrada;

    private final JLabel lblTitulo = new JLabel(" ");
    private final JLabel lblNome = new JLabel(" ");
    private final JLabel lblPartido = new JLabel(" ");
    private final JLabel lblAviso = new JLabel(" ");
    private final JLabel lblInstrucao = new JLabel(" ");
    private final JLabel lblFim = new JLabel("FIM", SwingConstants.LEFT);
    private final JLabel lblFoto = new JLabel();
    private final CardLayout cartoes = new CardLayout();
    private final JPanel areaNumero = new JPanel(cartoes);
    private final PainelArredondado[] caixas = new PainelArredondado[Urna.MAX_DIGITOS];
    private final JLabel[] digitosTela = new JLabel[Urna.MAX_DIGITOS];

    public UrnaFrame(Urna urna) {
        super("Urna Eletrônica");
        this.urna = urna;
        carregarFotos();
        construirInterface();
        atualizarTela();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private static Font fonte(int estilo, int tamanho) {
        return new Font(FAMILIA, estilo, tamanho);
    }

    private Cargo cargoAtual() {
        return cargos[cargoIndex];
    }

    private void construirInterface() {
        PainelArredondado corpo = new PainelArredondado(new BorderLayout(20, 0), CORPO_TOPO, CORPO_BASE, null, 0);
        corpo.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setContentPane(corpo);
        add(construirTela(), BorderLayout.CENTER);
        add(construirTeclado(), BorderLayout.EAST);
    }

    private JPanel construirTela() {
        PainelArredondado tela = new PainelArredondado(new BorderLayout(0, 18), TELA_TOPO, TELA_BASE,
                new Color(150, 160, 180), 30);
        tela.setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));
        tela.setPreferredSize(new Dimension(620, 460));
        tela.add(construirCabecalho(), BorderLayout.NORTH);
        tela.add(construirConteudo(), BorderLayout.CENTER);
        tela.add(construirRodape(), BorderLayout.SOUTH);
        return tela;
    }

    private JPanel construirCabecalho() {
        PainelArredondado cabecalho = new PainelArredondado(new BorderLayout(), AZUL, AZUL_ESCURO, null, 18);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 8));
        cabecalho.setPreferredSize(new Dimension(0, 44));

        JLabel titulo = new JLabel("URNA ELETRÔNICA");
        titulo.setFont(fonte(Font.BOLD, 15));
        titulo.setForeground(Color.WHITE);

        BotaoUrna mesario = new BotaoUrna("Mesário", new Color(255, 255, 255, 45), Color.WHITE,
                fonte(Font.BOLD, 13), 0, 14);
        mesario.setPreferredSize(new Dimension(96, 30));

        JPopupMenu menu = new JPopupMenu();
        JMenuItem itemEncerrar = new JMenuItem("Encerrar votação e ver resultado");
        itemEncerrar.addActionListener(e -> encerrarVotacao());
        menu.add(itemEncerrar);
        mesario.addActionListener(e ->
                menu.show(mesario, mesario.getWidth() - menu.getPreferredSize().width, mesario.getHeight()));

        JPanel direita = new JPanel(new GridBagLayout());
        direita.setOpaque(false);
        direita.add(mesario);

        cabecalho.add(titulo, BorderLayout.WEST);
        cabecalho.add(direita, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel construirConteudo() {
        lblTitulo.setFont(fonte(Font.BOLD, 13));
        lblTitulo.setForeground(TEXTO_SUAVE);
        lblNome.setFont(fonte(Font.BOLD, 26));
        lblPartido.setFont(fonte(Font.PLAIN, 17));
        lblPartido.setForeground(TEXTO_SUAVE);
        lblAviso.setFont(fonte(Font.BOLD, 24));
        lblFim.setFont(fonte(Font.BOLD, 60));
        lblFim.setForeground(VERDE);

        JLabel[] rotulos = {lblTitulo, lblNome, lblPartido, lblAviso};
        for (JLabel rotulo : rotulos) {
            rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        JPanel painelDigitos = new JPanel();
        painelDigitos.setLayout(new BoxLayout(painelDigitos, BoxLayout.X_AXIS));
        painelDigitos.setOpaque(false);
        for (int i = 0; i < Urna.MAX_DIGITOS; i++) {
            JLabel digito = new JLabel(" ", SwingConstants.CENTER);
            digito.setFont(fonte(Font.BOLD, 40));
            digito.setForeground(TEXTO);
            PainelArredondado caixa = new PainelArredondado(new BorderLayout(), Color.WHITE, Color.WHITE, LINHA, 16);
            caixa.setPreferredSize(new Dimension(62, 76));
            caixa.setMaximumSize(new Dimension(62, 76));
            caixa.add(digito, BorderLayout.CENTER);
            digitosTela[i] = digito;
            caixas[i] = caixa;
            painelDigitos.add(caixa);
            if (i < Urna.MAX_DIGITOS - 1) {
                painelDigitos.add(Box.createHorizontalStrut(10));
            }
        }
        painelDigitos.add(Box.createHorizontalGlue());

        JPanel vazio = new JPanel();
        vazio.setOpaque(false);

        areaNumero.setOpaque(false);
        areaNumero.add(painelDigitos, "numero");
        areaNumero.add(lblFim, "fim");
        areaNumero.add(vazio, "vazio");
        areaNumero.setAlignmentX(Component.LEFT_ALIGNMENT);
        areaNumero.setPreferredSize(new Dimension(300, 92));
        areaNumero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.add(lblTitulo);
        info.add(Box.createVerticalStrut(14));
        info.add(areaNumero);
        info.add(Box.createVerticalStrut(22));
        info.add(lblNome);
        info.add(Box.createVerticalStrut(6));
        info.add(lblPartido);
        info.add(Box.createVerticalStrut(18));
        info.add(lblAviso);
        info.add(Box.createVerticalGlue());

        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        PainelArredondado moldura = new PainelArredondado(new GridBagLayout(), Color.WHITE,
                new Color(238, 241, 246), LINHA, 26);
        moldura.setPreferredSize(new Dimension(FOTO_LARGURA + 16, FOTO_ALTURA + 16));
        moldura.add(lblFoto);

        JPanel painelFoto = new JPanel(new BorderLayout());
        painelFoto.setOpaque(false);
        painelFoto.add(moldura, BorderLayout.NORTH);

        JPanel conteudo = new JPanel(new BorderLayout(20, 0));
        conteudo.setOpaque(false);
        conteudo.add(info, BorderLayout.CENTER);
        conteudo.add(painelFoto, BorderLayout.EAST);
        return conteudo;
    }

    private JPanel construirRodape() {
        lblInstrucao.setFont(fonte(Font.ITALIC, 13));
        lblInstrucao.setForeground(TEXTO_SUAVE);
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, LINHA),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));
        rodape.add(lblInstrucao, BorderLayout.CENTER);
        return rodape;
    }

    private JPanel construirTeclado() {
        PainelArredondado teclado = new PainelArredondado(new BorderLayout(0, 16), new Color(78, 85, 102),
                new Color(60, 66, 82), new Color(96, 104, 122), 30);
        teclado.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        teclado.setPreferredSize(new Dimension(320, 460));

        JPanel numeros = new JPanel(new GridLayout(4, 3, 10, 10));
        numeros.setOpaque(false);
        String[] teclas = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", ""};
        for (String tecla : teclas) {
            if (tecla.isEmpty()) {
                numeros.add(new JLabel());
            } else {
                numeros.add(botao(tecla, new Color(26, 29, 36), Color.WHITE, 26, () -> digitar(tecla)));
            }
        }

        JPanel linha = new JPanel(new GridLayout(1, 2, 10, 0));
        linha.setOpaque(false);
        linha.add(botao("BRANCO", new Color(240, 241, 244), TEXTO, 15, this::votarBranco));
        linha.add(botao("CORRIGE", new Color(244, 130, 48), new Color(40, 22, 8), 15, this::corrigir));

        JPanel acoes = new JPanel(new GridLayout(2, 1, 0, 12));
        acoes.setOpaque(false);
        acoes.setPreferredSize(new Dimension(0, 140));
        acoes.add(linha);
        acoes.add(botao("CONFIRMA", new Color(56, 190, 98), new Color(8, 38, 18), 18, this::confirmar));

        teclado.add(numeros, BorderLayout.CENTER);
        teclado.add(acoes, BorderLayout.SOUTH);
        return teclado;
    }

    private BotaoUrna botao(String texto, Color fundo, Color letra, int tamanho, Runnable acao) {
        BotaoUrna b = new BotaoUrna(texto, fundo, letra, fonte(Font.BOLD, tamanho), 5, 18);
        b.addActionListener(e -> acao.run());
        return b;
    }

    private boolean bloqueado() {
        return finalizado || encerrada;
    }

    private void digitar(String digito) {
        if (bloqueado() || branco || digitos.length() >= cargoAtual().getDigitos()) {
            return;
        }
        digitos.append(digito);
        atualizarTela();
    }

    private void corrigir() {
        if (bloqueado()) {
            return;
        }
        digitos.setLength(0);
        branco = false;
        atualizarTela();
    }

    private void votarBranco() {
        if (bloqueado() || digitos.length() > 0) {
            return;
        }
        branco = true;
        atualizarTela();
    }

    private void confirmar() {
        if (bloqueado()) {
            return;
        }
        Cargo cargo = cargoAtual();
        if (branco) {
            urna.votarBranco(cargo);
        } else if (digitos.length() == cargo.getDigitos()) {
            Candidato candidato = urna.buscar(cargo, digitos.toString());
            if (candidato == null) {
                urna.votarNulo(cargo);
            } else {
                urna.votar(candidato);
            }
        } else {
            return;
        }
        avancar();
    }

    /** Passa para o próximo cargo; após o último, encerra o voto do eleitor. */
    private void avancar() {
        branco = false;
        digitos.setLength(0);
        if (cargoIndex < cargos.length - 1) {
            cargoIndex++;
            atualizarTela();
        } else {
            finalizarVoto();
        }
    }

    private void finalizarVoto() {
        finalizado = true;
        atualizarTela();
        Toolkit.getDefaultToolkit().beep();
        Timer timer = new Timer(2500, e -> {
            finalizado = false;
            branco = false;
            cargoIndex = 0;
            digitos.setLength(0);
            atualizarTela();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void atualizarTela() {
        Cargo cargo = cargoAtual();
        String titulo = "SEU VOTO PARA " + cargo.getRotulo();
        String confirmacao = "Aperte CONFIRMA para confirmar ou CORRIGE para refazer";
        if (encerrada) {
            mostrar("", "vazio", "", "", "VOTAÇÃO ENCERRADA", "");
            return;
        }
        if (finalizado) {
            mostrar("", "fim", "", "", "", "");
            return;
        }
        if (branco) {
            mostrar(titulo, "vazio", "", "", "VOTO EM BRANCO", confirmacao);
            return;
        }
        if (digitos.length() < cargo.getDigitos()) {
            mostrar(titulo, "numero", "", "", "", "Digite o número do candidato");
            return;
        }
        Candidato candidato = urna.buscar(cargo, digitos.toString());
        if (candidato == null) {
            mostrar(titulo, "numero", "NÚMERO ERRADO", "", "VOTO NULO", confirmacao);
        } else {
            mostrar(titulo, "numero", candidato.getNome(), candidato.getDescricaoTela(), "", confirmacao);
            definirFoto(candidato);
        }
    }

    private void mostrar(String titulo, String cartao, String nome, String partido, String aviso, String instrucao) {
        definir(lblTitulo, titulo);
        definir(lblNome, nome);
        definir(lblPartido, partido);
        definir(lblAviso, aviso);
        lblNome.setForeground(nome.equals("NÚMERO ERRADO") ? VERMELHO : TEXTO);
        lblAviso.setForeground(aviso.equals("VOTO NULO") ? VERMELHO : AZUL);
        atualizarCaixas();
        cartoes.show(areaNumero, cartao);
        String conteudo = instrucao.isEmpty() ? "&nbsp;" : instrucao;
        lblInstrucao.setText("<html><body style='width:570px'>" + conteudo + "</body></html>");
        definirFoto(null);
    }

    private void atualizarCaixas() {
        int total = cargoAtual().getDigitos();
        for (int i = 0; i < Urna.MAX_DIGITOS; i++) {
            boolean preenchida = i < digitos.length();
            boolean ativa = i == digitos.length();
            caixas[i].setVisible(i < total);
            digitosTela[i].setText(preenchida ? String.valueOf(digitos.charAt(i)) : " ");
            caixas[i].setBorda(ativa ? AZUL : (preenchida ? new Color(110, 122, 146) : LINHA));
        }
    }

    private void definir(JLabel rotulo, String texto) {
        rotulo.setText(texto.isEmpty() ? " " : texto);
    }

    private void definirFoto(Candidato candidato) {
        lblFoto.setIcon(candidato == null ? null : fotos.get(candidato.getNumero()));
    }

    private void encerrarVotacao() {
        if (encerrada) {
            mostrarResultado();
            return;
        }
        JPasswordField campo = new JPasswordField();
        int opcao = JOptionPane.showConfirmDialog(this, campo, "Senha do mesário",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }
        if (!SENHA_MESARIO.equals(new String(campo.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Senha incorreta.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        encerrada = true;
        urna.encerrar();
        atualizarTela();
        mostrarResultado();
    }

    private void mostrarResultado() {
        StringBuilder sb = new StringBuilder();
        for (Cargo cargo : cargos) {
            sb.append(String.format("=== %s ===%n", cargo.getRotulo()));
            for (Candidato c : urna.ranking(cargo)) {
                sb.append(String.format("%-4s %-18s %-16s %5d%n",
                        c.getNumero(), c.getNome(), c.getPartido(), c.getVotos()));
            }
            sb.append(String.format("Brancos: %d | Nulos: %d | Total: %d%n%n",
                    urna.getBrancos(cargo), urna.getNulos(cargo), urna.totalVotos(cargo)));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JOptionPane.showMessageDialog(this, area, "Resultado da votação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void carregarFotos() {
        for (Candidato candidato : urna.todosCandidatos()) {
            BufferedImage imagem = lerImagem(candidato.getNumero());
            if (imagem == null) {
                System.err.println("Foto não encontrada para " + candidato.getNome()
                        + " (esperado: " + PASTA_FOTOS + "/" + candidato.getNumero() + ".jpg ou .png)");
                imagem = gerarAvatar(candidato);
            }
            fotos.put(candidato.getNumero(), new ImageIcon(ajustar(imagem, FOTO_LARGURA, FOTO_ALTURA)));
        }
    }

    private BufferedImage lerImagem(String numero) {
        for (String extensao : EXTENSOES) {
            File arquivo = new File(PASTA_FOTOS, numero + "." + extensao);
            if (arquivo.isFile()) {
                try {
                    BufferedImage imagem = ImageIO.read(arquivo);
                    if (imagem != null) {
                        return imagem;
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao ler " + arquivo.getPath() + ": " + e.getMessage());
                }
            }
        }
        return null;
    }

    private BufferedImage ajustar(BufferedImage origem, int largura, int altura) {
        double escala = Math.max((double) largura / origem.getWidth(), (double) altura / origem.getHeight());
        int novaLargura = (int) Math.round(origem.getWidth() * escala);
        int novaAltura = (int) Math.round(origem.getHeight() * escala);
        BufferedImage destino = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = destino.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Float(0, 0, largura, altura, 22, 22));
        g.setComposite(AlphaComposite.SrcIn);
        g.drawImage(origem, (largura - novaLargura) / 2, (altura - novaAltura) / 2, novaLargura, novaAltura, null);
        g.dispose();
        return destino;
    }

    private BufferedImage gerarAvatar(Candidato candidato) {
        BufferedImage imagem = new BufferedImage(FOTO_LARGURA, FOTO_ALTURA, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(90, 110, 140));
        g.fillRect(0, 0, FOTO_LARGURA, FOTO_ALTURA);
        g.setColor(Color.WHITE);
        g.setFont(fonte(Font.BOLD, 56));
        String iniciais = iniciais(candidato.getNome());
        FontMetrics medidas = g.getFontMetrics();
        int x = (FOTO_LARGURA - medidas.stringWidth(iniciais)) / 2;
        int y = (FOTO_ALTURA + medidas.getAscent() - medidas.getDescent()) / 2;
        g.drawString(iniciais, x, y);
        g.dispose();
        return imagem;
    }

    private String iniciais(String nome) {
        String[] partes = nome.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        sb.append(partes[0].charAt(0));
        if (partes.length > 1) {
            sb.append(partes[partes.length - 1].charAt(0));
        }
        return sb.toString().toUpperCase();
    }
}