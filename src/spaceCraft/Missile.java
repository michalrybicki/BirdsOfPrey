package spaceCraft;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Missile {
	
    private String missile_pic = "pic/missile2_16.png";
    private Image image;
    private final int MISSILE_SPEED = 20;

    private float posX, posY, lastX, lastY;
    
    public float getLastX() {
		return lastX;
	}
	public float getLastY() {
		return lastY;
	}

	private int xVel;
    private int yVel;
    
    private int xWidth;
    private int yHeight;
    
    private int panelWidth;
    private int panelHeight;
	private boolean visible = true;
    

	public Missile(int lx, int ly, int direction) {

    	
        ImageIcon ii = new ImageIcon(this.getClass().getResource(missile_pic));
        image = ii.getImage();
        posX = lx;
        posY = ly;
        lastX = lx;
        lastY = ly;
        
        xVel = (int) (MISSILE_SPEED * Math.cos(Math.toRadians(direction)));
        yVel = (int) (MISSILE_SPEED * Math.sin(Math.toRadians(direction)));
        
        xWidth  = ii.getIconWidth();
        yHeight = ii.getIconHeight();
    }
	
    public Image getImage() {
        return image;
    }
    
    public float getX() {
        return posX;
    }

    public float getY() {
        return posY;
    }

    public int getWidth() {
		return xWidth;
	}

	public int getHeight() {
		return yHeight;
	}
    
	public void update(int pWidth, int pHeight) {
    	panelWidth  = pWidth;
    	panelHeight = pHeight;

    	lastX = posX;
    	lastY = posY;

		posX += xVel;
		posY += yVel;
		
		
		//If hits Right wall
    	if (posX  >= panelWidth + MISSILE_SPEED)
    	{
    		visible  = false;
    	}

    	//If hits Left Wall
    	else if (posX <= -MISSILE_SPEED)
    	{
    		visible = false;
    	}
    	//If hits bottom
    	if (posY >= panelHeight + MISSILE_SPEED)
    	{
    		visible = false;
    	}
    	//If hits top
    	else if (posY <= -MISSILE_SPEED)
    	{
    		visible = false;
    	}
		
	}
	public boolean isVisible() {

		return visible;
	}
    
}
