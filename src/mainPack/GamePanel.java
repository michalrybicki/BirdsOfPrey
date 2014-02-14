package mainPack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

class GamePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	float interpolation;

	private int frameCount = 0;
	private int fps = 0;
 
	int drawX;
	int drawY;

	
	private ArrayList<Craft> crafts;
	
	//---------------------------------
	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public int getFrameCount() {
		return frameCount;
	}

	public void zeroFrameCount() {
		this.frameCount = 0;
	}

	private class TAdapter extends KeyAdapter {

		public void keyReleased(KeyEvent e) {
			if ( ! crafts.isEmpty())
			{
				crafts.get(0).keyReleased(e);
			}
		}

		public void keyPressed(KeyEvent e) {
			if ( ! crafts.isEmpty())
			{
				crafts.get(0).keyPressed(e);
			}
		}
	}

	public GamePanel()
	{
		//-------------CRAFT--------------	  
		addKeyListener(new TAdapter());	
		crafts = new ArrayList<Craft>();
		 
		//--------------------------------
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);

	}
	
	public void addCraft(int i)
	{
		System.out.println("Adding craft=" + i);
		crafts.add(new Craft(i));
	}
	
	public ArrayList<Craft> getCrafts() {
		return crafts;
	}
	
	public void removeCrafts() {
		crafts.clear();
	}

	public void setInterpolation(float interp)
	{
		interpolation = interp;
	}

	public int getCraftInd(int cN)
	{
		for (Craft cR: crafts)
		{
			if( cN == cR.getCraftNumber())
				return crafts.indexOf(cR);
		}
		
		return -1;
	}
	
	public boolean isCraftPresent(int cN) 
	{
		for (Craft cR: crafts)
		{
			if( cN == cR.getCraftNumber())
				return true;
		}
		return false;
	}
	
	//Update direction and speed
	public void update()
	{
		//Update Crafts
		for (Craft craftTmp: crafts )
		{
			craftTmp.update(getWidth(),getHeight());

			//Update Missiles
			for (Missile ms: craftTmp.getMissiles()) {
				ms.update(getWidth(),getHeight());
			}	
		}
	}

	//Called by repaint()
	public void paint(Graphics g)
	{
		//Clear all.
		super.paint(g);
		 
		Graphics2D g2d = (Graphics2D)g;

		for (Craft craftTmp: crafts )
		{

			//Get craft image
			Image craftImage = craftTmp.getImage();

			// Draw Missiles
			ArrayList<Missile> msArray = craftTmp.getMissiles();
			for (int i=0; i < msArray.size(); i++) {
				Missile ms = (Missile) msArray.get(i);
				drawX = (int) ((ms.getX() - ms.getLastX()) * interpolation + ms.getLastX() - ms.getWidth()/2);
				drawY = (int) ((ms.getY() - ms.getLastY()) * interpolation + ms.getLastY() - ms.getHeight()/2);  
				if (ms.isVisible())
					g2d.drawImage(ms.getImage(), drawX, drawY, this);
				else msArray.remove(i);
			}

			//Draw Space Craft
			int drawCraftX = (int) ((craftTmp.getCraftX() - craftTmp.getLastX()) * interpolation + craftTmp.getLastX() - craftTmp.getWidth()/2);
			int drawCraftY = (int) ((craftTmp.getCraftY() - craftTmp.getLastY()) * interpolation + craftTmp.getLastY() - craftTmp.getHeight()/2);  
			g2d.drawImage(craftImage, drawCraftX, drawCraftY, this);
		}

		//Draw FPS Text
		g2d.setColor(Color.WHITE);
		g2d.drawString("FPS: " + fps, 10, 15);
		frameCount++;
	}

	
}