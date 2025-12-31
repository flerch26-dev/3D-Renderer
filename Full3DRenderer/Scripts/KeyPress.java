package Scripts;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyPress implements KeyListener
{
    public int key;

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        key = e.getKeyCode();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        key = '@';
    }
}