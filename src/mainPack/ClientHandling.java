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
				String craftsData = "";
				String inputLine = "";
				
				String[] cData = {"1","50.0","50.0","2"};
				float fX=(float) 50.0;
				float fY=(float) 50.0;
				int   iDir = 2;
				
				// Create Craft with next number. Send it to new client.
				int craftNumber=crafts.size();
				crafts.add(new Craft(craftNumber));
				outPW.println(craftNumber);
				outPW.flush();
				
				//Wait for data from Client
				while ((inputLine = inBR.readLine()) != null) 
				{
					//Get craft position
					System.out.println("Server got:  " + inputLine);

					if (inputLine.contains(";")) {
						cData=inputLine.split(";");
						System.out.println("CN=" + cData[0] + " x=" + cData[1] +" y=" + cData[2] + " d=" + cData[3] );
					}

					fX   = Float.parseFloat(cData[1]);
					fY   = Float.parseFloat(cData[2]);
					iDir = Integer.parseInt(cData[3]);
					crafts.get(craftNumber).setCraftX(fX);
					crafts.get(craftNumber).setCraftY(fY);
					crafts.get(craftNumber).setCraftDirection(iDir);

					//Send all crafts position
					for (Craft craftTmp: crafts) {
						fX= craftTmp.getCraftX();
						fY= craftTmp.getCraftY();
						iDir= craftTmp.getCraftDirection();
						craftsData += "=" + craftTmp.getCraftNumber() + ";" + fX + ";" + fY + ";" + iDir;
					}
					outPW.println(craftsData);
					outPW.flush();
					craftsData="";
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
