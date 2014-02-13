package mainPack;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;


public class Craft {

	private int craftNumber;
	private static final int RES_DEG = 15;
	private final int MAX_CRAFT_SPEED = 10;
	private static final int MAX_DIRECTIONS = 360/RES_DEG;
	private String craft = "pic/craft0/craft0_48_0.png";
	private Image image;
	private ImageIcon ii;
	private ArrayList<Missile> missiles;
    private ArrayList<Image> images;
    
    //-------------Position------------
    private float craftX, craftY, lastX, lastY;
    private int   craftWidth, craftHeight;
    private float craftXVel, craftYVel;
    private float craftSpeed;
    private float craftXSpeedMultipler = 1;
    private float craftYSpeedMultipler = 1;
    private int craftDirection;
    
   	
    private double lastTimeFired = System.currentTimeMillis();    	
    private double lastTimePressed = System.currentTimeMillis();
    //------------------------------
	private boolean rightTrue =false;
	private boolean leftTrue  =false;
	private boolean upTrue	  =false;
	private boolean downTrue  =false;
	private double now;
    private int panelWidth;
    private int panelHeight;
    
    
   
    public Craft(int i2) {
    	
    	this.craftNumber = i2;
    	
    	//Build Space craft
    	images = new ArrayList<Image>();
        for (int i=0; i<360; i+=RES_DEG) 
        {
        	craft="pic/craft0/craft0_48_" + i + ".png";	
        	ii = new ImageIcon(this.getClass().getResource(craft));
        	image = ii.getImage();
        	images.add(image);
        }
        image = images.get(0);

        craftWidth 	 = ii.getIconWidth();
        craftHeight  = ii.getIconHeight();
          
        //Initial position of Space Craft
        craftX = lastX = 200; 
        craftY = lastY = 200;
        
        //Set Craft Speed
        craftSpeed = 2;
        craftXVel = (float) Math.random() * craftSpeed*2 - craftSpeed; 
        craftYVel = (float) Math.random() * craftSpeed*2 - craftSpeed;
        
        //Load missiles
        missiles = new ArrayList<Missile>();
    }
    
    public float getX() {
        return craftX;
    }

    public float getY() {
        return craftY;
    }

    public Image getImage() {
        return image;
    }

    public void update(int pWidth, int pHeight){
    	panelWidth  = pWidth; 
    	panelHeight = pHeight;

    	now = System.currentTimeMillis();
    	if (now-lastTimePressed > 10)
    	{
    		if (rightTrue) {
    			craftDirection += 1;
    			craftDirection %= MAX_DIRECTIONS;			
    			image = images.get(craftDirection);
    		}

    		if (leftTrue) {
    			if (craftDirection == 0) {
    				craftDirection =  MAX_DIRECTIONS;
    			}
				craftDirection -= 1;
    			image = images.get(craftDirection);
    		}

    		if (upTrue) {
    			
    			craftXSpeedMultipler = 1;
      			craftYSpeedMultipler = 1;
    			
    			craftXVel += craftXSpeedMultipler*(Math.cos(Math.toRadians(craftDirection*RES_DEG)));
    			craftYVel += craftYSpeedMultipler*(Math.sin(Math.toRadians(craftDirection*RES_DEG)));
    		    			
    			System.out.println();
        			
    			if (craftXVel > MAX_CRAFT_SPEED) {
    				craftXVel = MAX_CRAFT_SPEED;
    			}
    			if (craftXVel < 0 && Math.abs(craftXVel) > MAX_CRAFT_SPEED) {
    				craftXVel = -MAX_CRAFT_SPEED;
    			}
    			
    			if (craftYVel > MAX_CRAFT_SPEED) {
    				craftYVel = MAX_CRAFT_SPEED;
    			}
    			if (craftYVel < 0 && Math.abs(craftXVel) > MAX_CRAFT_SPEED) {
    				craftYVel = -MAX_CRAFT_SPEED;
    			}
    		}

    		if (downTrue) 
    		{
    			craftXSpeedMultipler = 1;
      			craftYSpeedMultipler = 1;
    			
    			craftXVel -= craftXSpeedMultipler*(Math.cos(Math.toRadians(craftDirection*RES_DEG)));
    			craftYVel -= craftYSpeedMultipler*(Math.sin(Math.toRadians(craftDirection*RES_DEG)));
    		    			
    			System.out.println();
        			
    			if (craftXVel > MAX_CRAFT_SPEED) {
    				craftXVel = MAX_CRAFT_SPEED;
    			}
    			if ((craftXVel < 0) && (Math.abs(craftXVel) > MAX_CRAFT_SPEED)) {
    				craftXVel = -MAX_CRAFT_SPEED;
    			}
    			
    			if (craftYVel > MAX_CRAFT_SPEED) {
    				craftYVel = MAX_CRAFT_SPEED;
    			}
    			if (craftYVel < 0 && Math.abs(craftXVel) > MAX_CRAFT_SPEED) {
    				craftYVel = -MAX_CRAFT_SPEED;
    			}			
    		}
    		lastTimePressed = System.currentTimeMillis();
    	}

    	lastX = craftX;
    	lastY = craftY;

    	craftX += craftXVel; 
    	craftY += craftYVel;

    	//If hits Right wall
    	if (craftX + craftWidth/2 >= panelWidth)
    	{
    		craftXVel = -1;
    	}

    	//If hits Left Wall
    	else if (craftX - craftWidth/2 <= 0)
    	{
    		craftXVel = 1;
    	}
    	//If hits bottom
    	if (craftY + craftHeight/2 >= panelHeight)
    	{
    		craftYVel = -1;
    	}
    	//If hits top
    	else if (craftY - craftHeight/2 <= 0)
    	{
    		craftYVel = 1;
    	}
    }
    
	public float getLastX() {
		return lastX;
	}
	
	public float getLastY() {
		return lastY;
	}

	public int getWidth() {
		return craftWidth;
	}
    
	public int getHeight() {
		return craftHeight;
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		 if (key == KeyEvent.VK_RIGHT) {
	    		rightTrue = false;
	    	}
	    	
	        if (key == KeyEvent.VK_LEFT) {
	        	leftTrue = false;
	        }

	        if (key == KeyEvent.VK_UP) {
	        	upTrue = false;
	        }

	        if (key == KeyEvent.VK_DOWN) {
	        	downTrue = false;
	        }
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
        
		now = System.currentTimeMillis();
		
        if (key == KeyEvent.VK_SPACE){
        	if (now-lastTimeFired > 500) {
        		fire();
            	lastTimeFired = System.currentTimeMillis();
        	}
        }
        
        if (key == KeyEvent.VK_RIGHT) {
    		rightTrue = true;
    	}
    	
        if (key == KeyEvent.VK_LEFT) {
        	leftTrue = true;
        }

        if (key == KeyEvent.VK_UP) {
        	upTrue = true;
        }

        if (key == KeyEvent.VK_DOWN) {
        	downTrue = true;
        }
	}

	public void setCraftX(float craftX) {
		this.craftX = craftX;
	}

	//Take no prisoners!
    public void fire() {
    	missiles.add(new Missile((int)craftX,(int)craftY, craftDirection*RES_DEG));
    }
    
    public ArrayList<Missile> getMissiles() {
        return missiles;
    }

	public int getCraftNumber() {
		return craftNumber;
	}

	public void setCraftNumber(int craftNumber) {
		this.craftNumber = craftNumber;
	}
    
}