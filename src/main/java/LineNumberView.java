import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/** Cabecera de filas liviana para mostrar números de línea en un JTextArea. */
public final class LineNumberView extends JComponent implements DocumentListener {
    private final JTextArea textArea;

    public LineNumberView(JTextArea textArea) {
        this.textArea = textArea;
        setFont(textArea.getFont());
        setForeground(new Color(107, 114, 128));
        setBackground(new Color(245, 247, 250));
        setOpaque(true);
        textArea.getDocument().addDocumentListener(this);
        updateSize();
    }

    private void updateSize() {
        int lines = Math.max(1, textArea.getLineCount());
        int digits = Math.max(3, String.valueOf(lines).length());
        int width = getFontMetrics(getFont()).charWidth('0') * digits + 16;
        setPreferredSize(new Dimension(width, Integer.MAX_VALUE));
        revalidate();
        repaint();
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Rectangle clip = graphics.getClipBounds();
        FontMetrics metrics = graphics.getFontMetrics(getFont());
        int lineHeight = metrics.getHeight();
        int ascent = metrics.getAscent();
        int start = Math.max(0, clip.y / lineHeight);
        int end = Math.min(textArea.getLineCount(), (clip.y + clip.height) / lineHeight + 1);
        graphics.setColor(getForeground());
        for (int line = start; line < end; line++) {
            String number = String.valueOf(line + 1);
            int x = getWidth() - metrics.stringWidth(number) - 8;
            graphics.drawString(number, x, line * lineHeight + ascent);
        }
    }

    @Override public void insertUpdate(DocumentEvent e) { updateSize(); }
    @Override public void removeUpdate(DocumentEvent e) { updateSize(); }
    @Override public void changedUpdate(DocumentEvent e) { updateSize(); }
}
