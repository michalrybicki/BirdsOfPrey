package mainPack;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandling implements Runnable {

	private Socket clientSocket;

	public ClientHandling(Socket s)
	{
		clientSocket = s;
	}

	@Override
	public void run()
	{
		try
		{
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
			BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			{
				String inputLine;
				System.out.println("While1" );
				
				//Wait for inputLine
				while ((inputLine = in.readLine()) != null) 
				{
					System.out.println("While_sendback");
					out.println(inputLine);
					out.flush();
					
					System.out.println("Server Got:" + inputLine);

					System.out.println("While_end");

				}
				System.out.println("While_dead");

			}
		} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
						+ clientSocket.getPort() + " or listening for a connection");
				System.out.println(e.getMessage());
			}
 
		}
	}
