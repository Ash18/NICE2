import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;

class LinePainter implements Highlighter{
    JTextPane jTextPane;
    Rectangle prev=null;
    boolean clear=false;
    int lineNumber=1;
    void setLineNumber(int lineNumber){
        this.lineNumber=lineNumber;
        clear=false;
    }
    @Override
    public void install(JTextComponent c) {
        jTextPane=(JTextPane) c;
    }

    @Override
    public void deinstall(JTextComponent c) {

    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.CYAN);
        try {
            System.out.println(Thread.currentThread());
            Rectangle rectangle=jTextPane.modelToView(jTextPane.getDocument().getDefaultRootElement().getElement(lineNumber-1).getStartOffset());
            if(!clear)
            g.fillRect(0,rectangle.y,jTextPane.getWidth(),rectangle.height);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object addHighlight(int p0, int p1, HighlightPainter p) throws BadLocationException {
        return null;
    }

    @Override
    public void removeHighlight(Object tag) {

    }

    @Override
    public void removeAllHighlights() {
        clear=true;
    }

    @Override
    public void changeHighlight(Object tag, int p0, int p1) throws BadLocationException {

    }

    @Override
    public Highlight[] getHighlights() {
        return new Highlight[0];
    }
}