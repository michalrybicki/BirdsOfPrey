package mainPack;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandling implements Runnable {

	private Socket clientSocket;
	private ArrayList<Craft> crafts;
	
	public ClientHandling(Socket s, ArrayList<Craft> craftList)
	{
		clientSocket = s;
		crafts = craftList;
	}

	@Override
	public void run()
	{
		try
		{
			PrintWriter outPW = new PrintWriter(clientSocket.getOutputStream(), true);                   
			BufferedReader inBR = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			{
				//Send initial data to client:
				String craftsData = "#N="+ crafts.size() +"#";
				String inputLine = "";
				
				for (Craft craftTmp: crafts) {
					craftsData +="cN="+ craftTmp.getCraftNumber() + ",cX="+ craftTmp.getX()+",cY="+craftTmp.getX() + ";";
				}
				outPW.println(craftsData);
				outPW.flush();
				
				// Create Craft with next Number
				crafts.add(new Craft(crafts.size()));
				
 
				//Wait for inputLine
				while ((inputLine = inBR.readLine()) != null) 
				{
					//Get craft position
					System.out.println("Server got:" + inputLine);
	
					float fX = Float.parseFloat(inputLine);
					 crafts.get(1).setCraftX(fX);
					//Send all crafts position
					outPW.println(craftsData);
					outPW.flush();
					
				}
				
				
				System.out.println("ClientHandling(): after while");

			}
		} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
						+ clientSocket.getPort() + " or listening for a connection");
				System.out.println(e.getMessage());
			}
 
		}
	}
