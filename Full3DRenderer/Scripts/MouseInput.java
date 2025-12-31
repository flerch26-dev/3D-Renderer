package Scripts;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class MouseInput implements MouseListener {

    public boolean clicked;

    @Override
    public void mouseClicked(MouseEvent arg0) { 
      
     }

     @Override
     public void mouseEntered(MouseEvent arg0) { }

     @Override
     public void mouseExited(MouseEvent arg0) { }

     @Override
     public void mousePressed(MouseEvent arg0) { 
      clicked = true;
     }

     @Override
     public void mouseReleased(MouseEvent arg0) { 
      clicked = false;
     }
}