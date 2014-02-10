package mainPack;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Game extends JFrame implements ActionListener,WindowListener
{

	private static final long serialVersionUID = 1L;
	private GamePanel gamePanel = new GamePanel();
	private JButton startButton = new JButton("Start");
	private JButton quitButton = new JButton("Quit");
	private JButton pauseButton = new JButton("Pause");
	private JRadioButton serverButton = new JRadioButton("Server");
	private JRadioButton clientButton = new JRadioButton("Client");
	private JTextField ipAddress = new JTextField("192.168.1.100", 10);
  
	private boolean running = false;
	private boolean paused = false;
	private int fps = 0;
	 
	 
	public Game()
	{
		super("SpaceCraft T2");
		setSize(800, 600);
		
		//Get Frame container
		Container container = getContentPane();
		container.setLayout(new BorderLayout()); 
		
		//Set settingsPanel
		JPanel settingsPanel = new JPanel();
		GroupLayout layout = new GroupLayout(settingsPanel);
		settingsPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		serverButton.setSelected(true);
		ipAddress.setEditable(false);
		ButtonGroup group = new ButtonGroup();
		group.add(serverButton);
		group.add(clientButton);
		
		//Set GroupLayout
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(startButton)
						.addComponent(serverButton)
						.addComponent(clientButton))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(pauseButton)
						.addComponent(ipAddress ))
				.addComponent(quitButton)      
		);
		layout.setVerticalGroup( layout.createSequentialGroup()
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		           .addComponent(startButton)
		           .addComponent(pauseButton)
				   .addComponent(quitButton)) 
		      .addComponent(serverButton)
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		           .addComponent(ipAddress)
		      .addComponent(clientButton))
		      
		);
		

		layout.linkSize(SwingConstants.HORIZONTAL,startButton,pauseButton, quitButton, ipAddress );
		
		container.add(gamePanel, BorderLayout.CENTER);
		container.add(settingsPanel, BorderLayout.PAGE_END);
		
		
		//Add Listeners
        addWindowListener(this); 

        startButton.addActionListener(this);
		pauseButton.addActionListener(this);
		quitButton.addActionListener(this);
		serverButton.addActionListener(this);
		clientButton.addActionListener(this);
		ipAddress.addActionListener(this);
		
		setFocusable(true);
		setVisible(true);
	}

	public static void main(String[] args)
	{
		new Game();
	}

	public void actionPerformed(ActionEvent e)
	{ 

		Object s = e.getSource();
		if (s == startButton)
		{
			running = !running;
			if (running)
			{
				startButton.setText("Stop");
				ipAddress.setEditable(false);
				clientButton.setEnabled(false);
				serverButton.setEnabled(false);
				
				if (clientButton.isSelected()) 
				{
					if(validateIPAddress(ipAddress.getText()))
					{	
						Color color= Color.BLACK; 
						ipAddress.setForeground(color);
					
						if(isIPReachable(ipAddress.getText()))
						{		
							System.out.println("Found IP.");
							System.out.println("Connecting to " + ipAddress.getText() );
						}
						else
						{
							System.out.println("IP not found.");
						}
					}
					else
					{
						Color color= Color.RED; 
						ipAddress.setForeground(color); 
						System.out.println("IP not correct ");
					}
				}	
					
				runGameLoop();
			}
			else
			{
				startButton.setText("Start");
				if (serverButton.isSelected()) 
				{
					ipAddress.setEditable(false);
				}
				else
				{
					ipAddress.setEditable(true);
				}
				clientButton.setEnabled(true);
				serverButton.setEnabled(true);
				System.out.println("Dissconected");
			}
		}
		else if (s == pauseButton)
		{
			paused = !paused;
			if (paused)
			{
				pauseButton.setText("Unpause");
			}
			else
			{
				pauseButton.setText("Pause");
			}
		}
		else if (s == quitButton)
		{
			System.exit(0);
		}		
		else if (s == clientButton)
		{	
			ipAddress.setEditable(true);
 
		}
		else if (s == serverButton)
		{
			ipAddress.setEditable(false);
		}
		
		else if (s == ipAddress)
		{
			if(validateIPAddress(ipAddress.getText()))
			{
				Color color = Color.BLACK;			    
				ipAddress.setForeground(color);  
				 
			}
			else
			{
				Color color= Color.RED; 
				ipAddress.setForeground(color); 
			}
			
			System.out.println("List txt");
		}
		//Set focus on GAME
		gamePanel.requestFocus();
	}

	//Create a new thread and run the gameLoop.
	public void runGameLoop()
	{
		Thread loop = new Thread()
		{
			public void run()
			{
				gameLoop();
			}
		};
		loop.start();
	}

	
	private void gameLoop()
	{
		//Game Speed.
		final double GAME_HERTZ = 25.0;

		//Calculate how many ns each frame should take for our target game hertz.
		final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
		
		//50 updates in 20fps  / 1000Hz
		//10 updates in 10fps  / 200Hz 
		final int MAX_UPDATES_BEFORE_RENDER = 10;
		double lastUpdateTime = System.nanoTime();
		double lastRenderTime = System.nanoTime();

		//Set FPS. 320 is the maximum FPS for this HardWare.
		final double TARGET_FPS = 60;
		final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

		//Finding FPS.
		int lastSecondTime = (int) (lastUpdateTime / 1000000000);

		while (running)
		{
			double now = System.nanoTime();
			int updateCount = 0;

			if (!paused)
			{
				//Updates before render.
				while( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER )
				{
					//Update direction and speed 
					updateGame();
					lastUpdateTime += TIME_BETWEEN_UPDATES;
					updateCount++;
				}

				//Render. Calculate interpolation for a smooth render.
				float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
				
				//Call repaint
				drawGame(interpolation);
				lastRenderTime = now;

				//Update the frames.
				int thisSecond = (int) (lastUpdateTime / 1000000000);
				if (thisSecond > lastSecondTime)
				{
					System.out.println("NEW SECOND " + thisSecond + " " + gamePanel.getFrameCount() + " Update Count=" + updateCount);
					fps = gamePanel.getFrameCount();
					gamePanel.setFps(fps);
					gamePanel.zeroFrameCount();
					lastSecondTime = thisSecond;
				}
 
				//Saves CPU resources.
				while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
				{
					Thread.yield();
					try {Thread.sleep(1);} catch(Exception e) {} 

					now = System.nanoTime();
				}
			}
		}
	}

	private void updateGame()
	{
		gamePanel.update();
	}

	private void drawGame(float interpolation)
	{
		gamePanel.setInterpolation(interpolation);
		gamePanel.repaint();
	}

	public boolean validateIPAddress(String ipAddress)
	{
		String[] octets = ipAddress.split("\\.");
		if(octets.length != 4)
		{
			return false;
		}
		for (String str : octets)
		{
			int i;
			try
			{
				i = Integer.parseInt(str);
			}
			catch(NumberFormatException ex)
			{ 
				  i=-1;
			}
			
			if ((i<0) || (i>255))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean isIPReachable(String ipAdd)
	{
		boolean reachable = false;

	    try {		
			InetAddress inet1 = InetAddress.getByName(ipAdd);
			reachable = inet1.isReachable(5000);
	    } catch (UnknownHostException e) 
	    {
	    	 
		} catch (IOException e){
			 
				
		}
		 
		return reachable;
	}
	
	
	
	@Override
	public void windowOpened(WindowEvent e) {
		System.out.println("windowOpened");
	}

	@Override
	public void windowClosing(WindowEvent e) {
		running = false;
		System.out.println("windowClosing");
		System.exit(0);
		dispose();
	}

	@Override
	public void windowClosed(WindowEvent e) {

		running = false;
		System.out.println("windowClosed");
		System.exit(0);
		dispose();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		System.out.println("windowIconified");
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		System.out.println("windowDeiconified");
	}

	@Override
	public void windowActivated(WindowEvent e) {
		System.out.println("windowActivated");
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		System.out.println("windowDeactivated");
	}




}