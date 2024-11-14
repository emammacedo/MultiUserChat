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
			// servidor fica � espera de liga��es

			// Criar ServerSocket para escutar na porta 12345
			ServerSocket listen = new ServerSocket(12345); // liga��o, que vai ficar � escuta
			System.out.println("Listening for connection at port: " + listen.getLocalPort());

			while (true)
			{
				// Esperar pela ligação de um cliente
				Socket clientSocket = listen.accept(); // server oskcet tem metodo accept que vai ficar � escuta de
														// liga��es, este metodo � bloqueante, paa a execu��o e so
														// avan�a quando chega um pedido de conexao no porto 12345
				System.out.println("Received a connection...");

				// Delegar a comunicação com o cliente num theread e voltar a esperar por outro cliente
				Thread c = new Thread(new ConnectionHandler(clientSocket, channel, utilizadores, mensagens)); // thread � uma
																										// linha de
																										// execu��o
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
