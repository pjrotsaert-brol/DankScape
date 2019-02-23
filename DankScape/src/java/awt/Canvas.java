package java.awt;

import dankscape.api.Input;
import dankscape.api.internal.Projection;
import dankscape.api.rs.RSGameEngine;
import dankscape.bot.gui.BotGUI;
import dankscape.loader.AppletLoader;
import dankscape.bot.TaskScheduler;
import dankscape.misc.DankKeyEvent;
import dankscape.misc.DankMouseEvent;
import dankscape.misc.DankMouseWheelEvent;
import dankscape.nativeinterface.NativeInterface;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.peer.CanvasPeer;
import java.util.Arrays;

/**
 * Created by Brandon on 2015-12-20.
 */
public class Canvas extends Component implements Accessible {
    private static final long serialVersionUID = -2284879212465893870L;
    private static int nameCounter = 0;
    private static final String base = "canvas";

    private BufferedImage debugImage;

    public Canvas() {
        AppletLoader.getSingleton().onCanvasReady(this);
    }

    public Canvas(GraphicsConfiguration config) {
        this();
        setGraphicsConfiguration(config);
    }
    
    @Override
    public void processKeyEvent(KeyEvent e)
    {
        /*if(e.getID() == KeyEvent.KEY_PRESSED && e.getKeyChar() == 'o')
        {
            //0.000117569956287061224
            Projection.fovScaleFactor -= 0.0000001;
        }
        if(e.getID() == KeyEvent.KEY_PRESSED && e.getKeyChar() == 'p')
        {
            //0.000117569956287061224
            Projection.fovScaleFactor += 0.0000001;
        }*/
        
        if(e instanceof DankKeyEvent || BotGUI.inputEnabled)
            super.processKeyEvent(e);
    }
    
    @Override
    protected void processMouseEvent(MouseEvent e)
    {
        if(e.getID() == MouseEvent.MOUSE_PRESSED)
        {
            if(e.getButton() == MouseEvent.BUTTON1)
                Input.leftDown = true;
            if(e.getButton() == MouseEvent.BUTTON2)
                Input.middleDown = true;
            if(e.getButton() == MouseEvent.BUTTON3)
                Input.rightDown = true;
        }
        else if(e.getID() == MouseEvent.MOUSE_RELEASED)
        {
            if(e.getButton() == MouseEvent.BUTTON1)
                Input.leftDown = false;
            if(e.getButton() == MouseEvent.BUTTON2)
                Input.middleDown = false;
            if(e.getButton() == MouseEvent.BUTTON3)
                Input.rightDown = false;
        }
        
        if(e instanceof DankMouseEvent || BotGUI.inputEnabled)
            super.processMouseEvent(e);
    }
    
    public void sendMouseMotionEvent(MouseEvent e)
    {
        processMouseMotionEvent(e);
    }
    
    public void sendMouseEvent(MouseEvent e)
    {
        processMouseEvent(e);
    }
    
    @Override
    protected void processMouseMotionEvent(MouseEvent e)
    {
        //Debug.println("Motion Event: " + e.toString());
        if(e instanceof DankMouseEvent || BotGUI.inputEnabled)
        {
            if(!(e instanceof DankMouseEvent))
                Input.onUserMouseMoved(e.getX(), e.getY());
            else
            {
                Input.mouseX = e.getX();
                Input.mouseY = e.getY();
            }
            
            super.processMouseMotionEvent(e);
            //Arrays.stream(this.getListeners(MouseMotionListener.class)).forEach(l -> l.mouseMoved(e));
        }
    }
    
    public void sendMouseWheelEvent(MouseWheelEvent e)
    {
        processMouseWheelEvent(e);
    }
    
    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e)
    {
        //NativeInterface.println(e.toString());
        if(e instanceof DankMouseWheelEvent || BotGUI.inputEnabled)
            super.processMouseWheelEvent(e);
    }


    @Override
    public Graphics getGraphics() {
        if (debugImage == null || debugImage.getWidth() != getWidth() || debugImage.getHeight() != getHeight()) {
            debugImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        Graphics g = debugImage.getGraphics();
        
        /*if(RSGameEngine.getShell() != null)
        {
            try
            {
                if(this == RSGameEngine.getShell().getCanvas())
                    TaskScheduler.get().update(g);
            }
            catch(Exception ex)
            {
                NativeInterface.println("EXCEPTION IN PAINT THREAD: " + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
            }
        }*/
        
        if(super.getGraphics() != null)
            super.getGraphics().drawImage(debugImage, 0, 0, null);

        return g;
    }
    
    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, width, height);
    }

    @Override
    public void update(Graphics g) {
        g.clearRect(0, 0, width, height);
        paint(g);
    }
    
    public BufferedImage getImage()
    {
        return this.debugImage;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    @Override
    void setGraphicsConfiguration(GraphicsConfiguration gc) {
        synchronized(getTreeLock()) {
            CanvasPeer peer = (CanvasPeer)getPeer();
            if (peer != null) {
                gc = peer.getAppropriateGraphicsConfiguration(gc);
            }
            super.setGraphicsConfiguration(gc);
        }
    }

    @Override
    String constructComponentName() {
        synchronized (Canvas.class) {
            return base + nameCounter++;
        }
    }

    @Override
    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null)
                peer = getToolkit().createCanvas(this);
            super.addNotify();
        }
    }


    @Override
    boolean postsOldMouseEvents() {
        return true;
    }

    @Override
    public void createBufferStrategy(int numBuffers) {
        super.createBufferStrategy(numBuffers);
    }

    @Override
    public void createBufferStrategy(int numBuffers, BufferCapabilities caps) throws AWTException {
        super.createBufferStrategy(numBuffers, caps);
    }

    @Override
    public BufferStrategy getBufferStrategy() {
        return super.getBufferStrategy();
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTCanvas();
        }
        return accessibleContext;
    }

    protected class AccessibleAWTCanvas extends AccessibleAWTComponent {
        private static final long serialVersionUID = -6325592262103146699L;

        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CANVAS;
        }
    }
}