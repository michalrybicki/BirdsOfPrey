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
	float ballX, ballY, lastBallX, lastBallY;
	int   ballWidth, ballHeight;
	float ballXVel, ballYVel;
	float ballSpeed;
	private int frameCount = 0;
	private int fps = 0;
	int lastDrawX, lastDrawY;
	int drawX;
	int drawY;

	//-------------CRAFT---------------
	Craft craft;
	int lastDrawCraftX, lastDrawCraftY;
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
			craft.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			craft.keyPressed(e);
		}
	}

	public GamePanel()
	{

		ballX = lastBallX = 100;
		ballY = lastBallY = 100;
		ballWidth = 25;
		ballHeight = 25;
		ballSpeed = 70;
		ballXVel = (float) Math.random() * ballSpeed*2 - ballSpeed;
		ballYVel = (float) Math.random() * ballSpeed*2 - ballSpeed;

		//-------------CRAFT--------------	  
		addKeyListener(new TAdapter());
		craft = new Craft();

		//------------------------------
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);

	}

	public void setInterpolation(float interp)
	{
		interpolation = interp;
	}

	//Update direction and speed
	public void update()
	{

		lastBallX = ballX;
		lastBallY = ballY;

		//Next position
		ballX += ballXVel;
		ballY += ballYVel;

		//If hits Right wall
		if (ballX + ballWidth/2 >= getWidth())
		{    	
			ballXVel *= -1;
			ballX = getWidth() - ballWidth/2;
			ballYVel = (float) Math.random() * ballSpeed*2 - ballSpeed;
		}
		//If hits Left Wall
		else if (ballX - ballWidth/2 <= 0)
		{		   
			ballXVel *= -1;
			ballX = ballWidth/2;
		}

		//If hits bottom
		if (ballY + ballHeight/2 >= getHeight())
		{		   
			ballYVel *= -1;
			ballY = getHeight() - ballHeight/2;
			ballXVel = (float) Math.random() * ballSpeed*2 - ballSpeed;
		}
		//If hits up
		else if (ballY - ballHeight/2 <= 0)
		{		   
			ballYVel *= -1;
			ballY = ballHeight/2;
		}

		//Update Craft
		craft.update(getWidth(),getHeight());

		//Update Missiles
		for (Missile ms: craft.getMissiles()) {
			ms.update(getWidth(),getHeight());
		}	   
	}

	//Called by repaint()
	public void paint(Graphics g)
	{
		//Set background.
		super.paint(g);

		//requestFocus();
		Graphics2D g2d = (Graphics2D)g;

		Image craftImage = craft.getImage();
		//Image craftImage = craft.getImage().getScaledInstance(48, 48, 10);

		//Clear the old rectangle to save CPU.
		g2d.setColor(getBackground());
		//Cover last ball position
		g2d.fillRect(lastDrawX-1, lastDrawY-1, ballWidth+2, ballHeight+2);
		//Cover last Craft position
		g2d.fillRect(lastDrawCraftX-1, lastDrawCraftY-1, craft.getWidth()+2, craft.getHeight()+2);
		//Cover old text field
		g2d.fillRect(5, 0, 75, 30);

		//Draw new Ball position
		g2d.setColor(Color.RED);
		drawX = (int) ((ballX - lastBallX) * interpolation + lastBallX - ballWidth/2);
		drawY = (int) ((ballY - lastBallY) * interpolation + lastBallY - ballHeight/2);
		g2d.fillOval(drawX, drawY, ballWidth, ballHeight);

		// Draw Missiles
		ArrayList<Missile> msArray = craft.getMissiles();
		for (int i=0; i < msArray.size(); i++) {
			Missile ms = (Missile) msArray.get(i);
			drawX = (int) ((ms.getX() - ms.getLastX()) * interpolation + ms.getLastX() - ms.getWidth()/2);
			drawY = (int) ((ms.getY() - ms.getLastY()) * interpolation + ms.getLastY() - ms.getHeight()/2);  
			if (ms.isVisible())
				g2d.drawImage(ms.getImage(), drawX, drawY, this);
			else msArray.remove(i);
		}

		//Draw Space Craft
		int drawCraftX = (int) ((craft.getX() - craft.getLastX()) * interpolation + craft.getLastX() - craft.getWidth()/2);
		int drawCraftY = (int) ((craft.getY() - craft.getLastY()) * interpolation + craft.getLastY() - craft.getHeight()/2);  
		g2d.drawImage(craftImage, drawCraftX, drawCraftY, this);

		lastDrawCraftX = drawCraftX;
		lastDrawCraftY = drawCraftY;

		lastDrawX = drawX;
		lastDrawY = drawY;

		//Draw Text
		g2d.setColor(Color.BLACK);
		g2d.drawString("FPS: " + fps, 5, 10);
		g2d.dispose();
		frameCount++;
	}
}