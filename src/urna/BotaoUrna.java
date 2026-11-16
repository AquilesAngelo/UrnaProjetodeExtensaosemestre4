package urna;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotaoUrna extends JButton {

    private final Color fundo;
    private final int profundidade;
    private final int raio;
    private boolean sobre;

    public BotaoUrna(String texto, Color fundo, Color letra, Font fonte, int profundidade, int raio) {
        super(texto);
        this.fundo = fundo;
        this.profundidade = profundidade;
        this.raio = raio;
        setFont(fonte);
        setForeground(letra);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sobre = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sobre = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int largura = getWidth();
        int altura = getHeight();
        int alturaFace = altura - profundidade;
        int deslocamento = getModel().isPressed() ? Math.min(profundidade, 2) : 0;
        Color cor = sobre ? clarear(fundo, 18) : fundo;
        if (profundidade > 0) {
            g2.setColor(escurecer(fundo, 0.55f));
            g2.fillRoundRect(0, profundidade, largura, alturaFace, raio, raio);
        }
        g2.setPaint(new GradientPaint(0, deslocamento, clarear(cor, 14), 0, deslocamento + alturaFace, cor));
        g2.fillRoundRect(0, deslocamento, largura, alturaFace, raio, raio);
        g2.setFont(getFont());
        g2.setColor(getForeground());
        FontMetrics medidas = g2.getFontMetrics();
        int x = (largura - medidas.stringWidth(getText())) / 2;
        int y = deslocamento + (alturaFace - medidas.getHeight()) / 2 + medidas.getAscent();
        g2.drawString(getText(), x, y);
        g2.dispose();
    }

    private static Color clarear(Color c, int quantidade) {
        return new Color(Math.min(255, c.getRed() + quantidade),
                Math.min(255, c.getGreen() + quantidade),
                Math.min(255, c.getBlue() + quantidade),
                c.getAlpha());
    }

    private static Color escurecer(Color c, float fator) {
        return new Color((int) (c.getRed() * fator),
                (int) (c.getGreen() * fator),
                (int) (c.getBlue() * fator),
                c.getAlpha());
    }
}