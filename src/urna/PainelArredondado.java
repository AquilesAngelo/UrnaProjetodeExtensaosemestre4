package urna;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

public class PainelArredondado extends JPanel {

    private final Color topo;
    private final Color base;
    private Color borda;
    private final int raio;

    public PainelArredondado(LayoutManager layout, Color topo, Color base, Color borda, int raio) {
        super(layout);
        this.topo = topo;
        this.base = base;
        this.borda = borda;
        this.raio = raio;
        setOpaque(raio == 0 && topo != null);
    }

    public void setBorda(Color borda) {
        this.borda = borda;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int largura = getWidth();
        int altura = getHeight();
        if (topo != null) {
            g2.setPaint(new GradientPaint(0, 0, topo, 0, altura, base));
            g2.fillRoundRect(0, 0, largura, altura, raio, raio);
        }
        if (borda != null) {
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(borda);
            g2.drawRoundRect(1, 1, largura - 3, altura - 3, raio, raio);
        }
        g2.dispose();
    }
}