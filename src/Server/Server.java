package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
	public static void main(String[] args) 
	{
		
		CanalChat channel = new CanalChat("12345"); // cria o canal de Chat
		ArrayList<Utilizador> utilizadores = new ArrayList<Utilizador>(); // para guardar utilizadores
		ArrayList<Mensagem> mensagens = new ArrayList<Mensagem>();
		
		try
		{
			// servidor fica à espera de ligações

			// Criar ServerSocket para escutar na porta 12345
			ServerSocket listen = new ServerSocket(12345); // ligação, que vai ficar à escuta
			System.out.println("Listening for connection at port: " + listen.getLocalPort());

			while (true)
			{
				// Esperar pela ligaÃ§Ã£o de um cliente
				Socket clientSocket = listen.accept(); // server oskcet tem metodo accept que vai ficar à escuta de
														// ligações, este metodo é bloqueante, paa a execução e so
														// avança quando chega um pedido de conexao no porto 12345
				System.out.println("Received a connection...");

				// Delegar a comunicaÃ§Ã£o com o cliente num theread e voltar a esperar por outro cliente
				Thread c = new Thread(new ConnectionHandler(clientSocket, channel, utilizadores, mensagens)); // thread é uma
																										// linha de
																										// execução
																										// paralela,
																										// essa linha ->
																										// classe
																										// connection
				c.start(); // todas as classes thread tem um metodo start
				
			}
		}
		catch (Exception e)
		{
			System.out.println("Server terminated. Error:");
			e.printStackTrace();
		}
	}
}
