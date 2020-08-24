

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * FPS Mouse Capture Test.
 * 
 * References:
 * https://discourse.processing.org/t/solved-creating-fps-like-mouse-detection/11946
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class View extends Canvas {
    
    private BufferStrategy bs;
    
    private BufferedImage image;
    
    private boolean running;
    
    private FpsMouseCapturer fps;
    
    private final DecimalFormat df = new DecimalFormat("#0.000");
    
    public View() {
    }
    
    public void start() {
        fps = new FpsMouseCapturer();
        fps.install(this);
        
        final int width = getWidth() / 2;
        final int height = getHeight() / 2;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        createBufferStrategy(2);
        bs = getBufferStrategy();
        running = true;
        
        new Thread(() -> {
            while (running) {
                update();
                draw((Graphics2D) image.getGraphics());
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                g.drawImage(image, 0, 0, getWidth(), getHeight()
                    , 0, 0, width, height, null);
                
                g.dispose();
                bs.show();
                
                try {
                    Thread.sleep(1000 / 60);
                } catch (InterruptedException ex) {
                }
            }
        }).start();
    }

    private void update() {
        fps.update();
    }
    
    private void draw(Graphics2D g) {
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.BLACK);
        //g.drawLine(0, 0, getWidth(), getHeight());
        
        if (fps.isCaptureMouse()) {
            g.drawString("limit x: " + fps.isLimitX(), 50, 110);
            g.drawString("limit y: " + fps.isLimitY(), 50, 130);
            g.drawString("mouse value: (" 
                    + df.format(fps.getValue().getX()) + ","
                    + df.format(fps.getValue().getY()) + ")", 50, 150);
            
            g.drawString("Press ESC to uncapture mouse.", 10, 50);
        }
        else {
            
            g.drawString("Click to capture mouse.", 10, 50);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            view.setPreferredSize(new Dimension(800, 600));
            JFrame frame = new JFrame();
            frame.setTitle("FPS Mouse Capturer Test #1");
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }    
    
}
