package mainPack;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
	private JTextField ipAddress = new JTextField("192.168.1.106", 10);
	private static Socket serverNode;
	
	private volatile boolean running = false;
	private boolean paused = false;
	private int fps = 0;
	private ServerSocket serverSocket;
	 
	 
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

				if (serverButton.isSelected()) 
				{	
					startServer();
				}
				else
				if (clientButton.isSelected()) 
				{
					
					if(validateIPAddress(ipAddress.getText()))
					{	
						startClient();
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
					
					try {
						serverSocket.close();
					} catch (IOException e1) {
						 
					}
					
					ipAddress.setEditable(false);
					gamePanel.removeCrafts();
				}
				if (clientButton.isSelected())
				{
					ipAddress.setEditable(true);
					System.out.println("Client stoped.");
				}
				clientButton.setEnabled(true);
				serverButton.setEnabled(true);
				
				
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
				
				//Call repaint.
				drawGame(interpolation);
				lastRenderTime = now;

				//Update the frames.
				int thisSecond = (int) (lastUpdateTime / 1000000000);
				if (thisSecond > lastSecondTime)
				{
					//System.out.println("NEW SECOND " + thisSecond + " " + gamePanel.getFrameCount() + " Update Count=" + updateCount);
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
			try	{
				i = Integer.parseInt(str);
			}
			catch(NumberFormatException ex) { 
				  i=-1;
			}
			
			if ((i<0) || (i>255)) {
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
			reachable = inet1.isReachable(1000);
		} catch (UnknownHostException e) {
		} catch (IOException e){
		}

		return reachable;
	}
	
	private void startServer() {
		
		gamePanel.addCraft(0);
		
		Thread startServerThr = new Thread()
		{
			public void run()
			{
				try 
				{
					final int PORT = 6677;
					serverSocket = new ServerSocket(PORT);
					Socket clientSocket;

					while (true)
					{
						System.out.println("Waiting for clients...");
						clientSocket = serverSocket.accept();

						System.out.println("Client connected from " + clientSocket.getLocalAddress().getHostName());

						ClientHandling newClient = new ClientHandling(clientSocket, gamePanel.getCrafts());
						Thread t = new Thread(newClient);
						t.start();
					}
					
				} 
				catch(SocketTimeoutException e) {
					System.out.println("Socket timed out!");
				}
				catch(IOException e) {
					if (running) {
						System.out.println("Error: Server PORT already in use");
						e.printStackTrace();
					}
					else {
						System.out.println("Server for new clients closed.");
					}
				} catch ( Exception e) {
					System.out.println("Error:");
					e.printStackTrace();
				}
			}
		};
		startServerThr.start();
	}
	
	private void startClient() {

		Color color= Color.BLACK; 
		ipAddress.setForeground(color);

		Thread startClientThr = new Thread()
		{
			

			public void run()
			{
				if(isIPReachable(ipAddress.getText()) == false) {
					System.out.println("IP not found.");
					return;
				}
				
				String serverIP = ipAddress.getText();

				try {
					serverNode = new Socket(serverIP, 6677);
				}
				catch (Exception e) {
					System.out.println("Could not connect to:" + serverIP);	
				} 
				if (serverNode != null)
				{
					try {
						PrintWriter out = null;
						BufferedReader in = null;
						out = new PrintWriter(serverNode.getOutputStream());
						in = new BufferedReader( new InputStreamReader(serverNode.getInputStream()));
						
						int localCraftNumber;
						String inputLine = "";
						String[] cData;
						String[] cDA;
						
						//Get initial data from Server
						inputLine = in.readLine();
						System.out.println("Set craft number: " + inputLine);	
						
						//Add client Craft.
						int crr= Integer.parseInt(inputLine);
						gamePanel.addCraft(crr);
						
						//Add Server Craft.
						gamePanel.addCraft(0);
						
						System.out.println("Crafts size= " + gamePanel.getCrafts().size()); 
						System.out.println(" crr=" + crr );
						
						System.out.println(" CR0= " + gamePanel.getCrafts().get(0).getCraftNumber() );
						System.out.println(" CR1= " + gamePanel.getCrafts().get(1).getCraftNumber() );
						
						
						System.out.println(" CRR0= " + gamePanel.getCraftInd(Integer.parseInt("0")) );
						System.out.println(" CRR1= " + gamePanel.getCraftInd(Integer.parseInt("1")) );
						
						
						
						//Send first client Craft position
						float fX = gamePanel.getCrafts().get(1).getCraftX();
						float fY = gamePanel.getCrafts().get(1).getCraftY();
						int iDir = gamePanel.getCrafts().get(1).getCraftDirection();
						out.println(gamePanel.getCrafts().get(1).getCraftNumber()  + ";" + fX +";" + fY + ";" + iDir);
						out.flush();
						
						while (true)
						{
							if((inputLine = in.readLine()) != null) {
								System.out.println("Client got: " + inputLine);									
							}
							else {
								break;
							}

							if (inputLine.contains("=")) {
								cData = inputLine.split("=");
								for (String cD: cData)
								{
									if (cD.contains(";")) {
										cDA = cD.split(";");
										
										int remoteCraftNumber = Integer.parseInt(cDA[0]);
										int cInd = gamePanel.getCraftInd(remoteCraftNumber);
										System.out.println("pi-cN= " + remoteCraftNumber);
										System.out.println("pi-cN= " + cInd);
										
										System.out.println("CN=" + cDA[0] + " x=" + cDA[1] +" y=" + cDA[2] + " d=" + cDA[3] + " zz=" + cInd );
										localCraftNumber = gamePanel.getCrafts().get(0).getCraftNumber();
									
										if ((gamePanel.isCraftPresent(cInd)) && (remoteCraftNumber != localCraftNumber))
										{
											System.out.println("Settings, cInd=" + cInd + " remoteCraftNumber=" + remoteCraftNumber + "\n");
											fX   = Float.parseFloat(cDA[1]);
											fY   = Float.parseFloat(cDA[2]);
											iDir = Integer.parseInt(cDA[3]);
											
											//getCraftId
											gamePanel.getCrafts().get(cInd).setCraftX(fX);
											gamePanel.getCrafts().get(cInd).setCraftY(fY);
											gamePanel.getCrafts().get(cInd).setCraftDirection(iDir);
										}
										if (( gamePanel.isCraftPresent(remoteCraftNumber)) == false)
										{
											System.out.println("Added Craft = " + remoteCraftNumber);
											gamePanel.addCraft(remoteCraftNumber);
										}
									}	
								}
							}
							
							
							System.out.println("Sending XY to server");
							System.out.println("");
							
							fX= gamePanel.getCrafts().get(0).getCraftX();
							fY= gamePanel.getCrafts().get(0).getCraftY();
							iDir= gamePanel.getCrafts().get(0).getCraftDirection();
							out.println(gamePanel.getCrafts().get(0).getCraftNumber()  + ";" + fX +";" + fY + ";" + iDir);
							out.flush();
						}
						serverNode.close();
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};

		startClientThr.start();
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		//System.out.println("windowOpened");
	}

	@Override
	public void windowClosing(WindowEvent e) {
		running = false;
		//System.out.println("windowClosing");
		System.exit(0);
		dispose();
	}

	@Override
	public void windowClosed(WindowEvent e) {

		running = false;
		//System.out.println("windowClosed");
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
		//System.out.println("windowActivated");
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		//System.out.println("windowDeactivated");
	}

}