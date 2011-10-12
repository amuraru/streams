package net.scinotes.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


/**
 * This class is a variant of the JPanel class which can be used to display an image. 
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class ImagePanel
extends JPanel
{

    private static final long serialVersionUID = 793939048646429608L;

    /** The image to be displayed */
    private Image img;
    
    int height = -1, width = -1;
    int x0, y0;
    int w, h;
    boolean scale = false;

    /**
     * This creates a new instance of this panel using the image from the given url. The
     * image will be scaled to the dimensions determined by <code>width</code> and <code>height</code>.  
     * 
     * 
     * @param url The url of the image resource.
     * @param mX 
     * @param mY
     * @param width The width of the image (and this panel).
     * @param height The height of the image (and this panel).
     */
    public ImagePanel(URL url, int mX, int mY, int width, int height, boolean scale ){
        x0 = mX;
        y0 = mY;
        w = width;
        h = height;
        this.scale = scale;
       
        this.setOpaque(true);
        setSize(height,width);
        setVisible(true);
        setPreferredSize(new Dimension(2*mX+height,2*mY+width));
        setMinimumSize(new Dimension(2*mX+height,2*mY+width));
        MediaTracker mt = new MediaTracker(this);
        setHeight(2*mX+height);
        setWidth(2*mY+width);
        
        try {
            img = ImageIO.read(url);
            mt.addImage(img, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            mt.waitForAll();
        } catch (InterruptedException e) {
        }
        repaint();
    }
    
    public ImagePanel( URL url, int mX, int mY, int width, int height){
        this( url, mX, mY, width, height, true );
    }
    
    
    /**
     * 
     * @param url
     * @deprecated
     */
    public ImagePanel(URL url)
    {
        this(url, 0, 0, 80, 80);
    }

    /**
     * Sets the height of this panel to the given value
     * 
     * @param h The new height.
     */
    public void setHeight(int h){
        height = h;
    }
    
    public void paint(Graphics g)
    {
        if (img != null) {
            if( scale )
                g.drawImage( img, x0, y0, getWidth() - x0, getHeight() - y0, this );
            else
                g.drawImage( img, x0, y0, this );
        }
    }
    

    /**
     * @see javax.swing.JComponent#getHeight()
     */
    @Override
    public int getHeight()
    {
        if(height == -1)
            return super.getHeight();
        
        return height;
    }
    
    
    /**
     * Sets the width of the panel to the given value.
     * 
     * @param w The new width.
     */
    public void setWidth(int w){
        width = w;
    }

    
    /**
     * @see javax.swing.JComponent#getWidth()
     */
    public int getWidth(){
        if(width == -1)
            return super.getWidth();
        
        return width;
    }
    
    /**
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize()
    {
        int ph = getHeight();
        int pw = getWidth();
        
        return new Dimension(pw, ph);
    }
}
