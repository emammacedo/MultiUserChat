package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
	public static void main(String[] args) throws IOException
	{
		Scanner sc = new Scanner(System.in);
		
		//String username;
		//String password;
		//String resposta_servidor;
		//String send_str;

		boolean continuar = true;
		do
		{
			String username;
			String password;
			String resposta_servidor;
			String send_str;
			
			boolean login = false;
			boolean pass_ver = false;

			// Criar ligação socket ao servidor
			Socket serverCon = new Socket("localhost", 12345); // ligar ao servidor no mesmo porto 12345

			// Criar canais de comunicação
			DataInputStream in = new DataInputStream(serverCon.getInputStream());
			DataOutputStream out = new DataOutputStream(serverCon.getOutputStream());

			while (login == false)
			{
				do
				{
					System.out.print("******************* SIChat *******************\n");
					System.out.print("Insert USER NAME:");
					username = sc.nextLine();

					// Enviar username para o servidor
					out.writeUTF("UTILIZADOR:" + username);
					out.flush();

					System.out.print("Insert Chat Password:");
					password = sc.nextLine();

					// Enviar pass para o servidor
					out.writeUTF("PASSWORD:" + password);
					out.flush();

					// Verificação password
					resposta_servidor = in.readUTF();
					if (resposta_servidor.contentEquals("WRONG PASS"))
					{
						System.out.print("=============== Wrong Password ===============\n\n");
					}
					else
					{
						pass_ver = true;
					}
				}
				while (pass_ver == false);

				// Verificação username
				resposta_servidor = in.readUTF();
				if (resposta_servidor.contentEquals("ALREADY LOGGED"))
				{
					System.out.print("=============== " + username + " allready logged!! ===============\n");
					continue;
				}
				else // tudo ok (resposta servidor = NEW USER / OLD USER)
				{

					System.out.print("=============== Welcome " + username + " ===============\n\n");
					login = true;

					System.out.println("Type #H at any time to see all messages sent on the chat\n");

					// Mostrar mensagens NÃO LIDAS
					resposta_servidor = in.readUTF();
					if (resposta_servidor.contentEquals("SHOW"))
					{
						do
						{
							resposta_servidor = in.readUTF();
							if (!resposta_servidor.equals("FINISH"))
								System.out.println(resposta_servidor);
						}
						while (!resposta_servidor.equals("FINISH"));
					}

					// Continua para o chat
					do
					{
						// menu utilizador
						System.out.print(
								username + "( Users List = #U | Num Unread = #K | Refresh chat = #R | Exit = exit ):> ");
						send_str = sc.nextLine();

						out.writeUTF(send_str); 
						out.flush();

						if (send_str.equals("#U"))
						{
							do
							{
								resposta_servidor = in.readUTF();
								if (!resposta_servidor.equals("FINISH"))
									System.out.println(resposta_servidor);
							}
							while (!resposta_servidor.equals("FINISH"));

						}
						else if (send_str.equals("#K"))
						{
							int n_unreadmsg = in.readInt();
							System.out.println("Unread Messages = " + n_unreadmsg + " (Type #R to view)");

						}
						else if (send_str.equals("#R"))
						{
							do
							{
								resposta_servidor = in.readUTF();
								if (!resposta_servidor.equals("FINISH"))
									System.out.println(resposta_servidor);
							}
							while (!resposta_servidor.equals("FINISH"));
						}
						else if (send_str.equals("#H"))
						{
							int m_ler = in.readInt();

							System.out.println("=================== History ==================");
							if (m_ler != 0)
								for (int i = 0; i < m_ler; i++)
									System.out.println(in.readUTF());
							System.out.println("=================== END History ==================");
						}
						else //utilizador envia mensagem para o chat
						{
							resposta_servidor = in.readUTF();
							if (resposta_servidor.contentEquals("MSG OK"))
							{
								System.out.print(username + ":> " + send_str); // print da mensagem enviada
								System.out.print("\n");
							}
						}
					}
					while (!send_str.toLowerCase().equals("exit")); // até que mensagem seja igual a exit
				}

				String server_resposta = in.readUTF();
				if (server_resposta.contentEquals("EXIT"))// quando é exit, sai e termina as ligação
				{
					System.out.println("Bye Bye!");
					System.out.println("C - Close or other symbol to continue?");
					String resposta = sc.nextLine();
					
					out.writeUTF(resposta); 
					out.flush(); 
				}

				// Verificar resposta do servidor
				server_resposta = in.readUTF();

				if (server_resposta.equals("LOGOUT"))
				{
					System.out.println("saiu");
					continuar = false;
					// Fechar canais de comunicação e socket
					in.close();
					out.close();
					serverCon.close();
					sc.close();
				}
				else
				{
					login = false; // para recomeçar ciclo
					pass_ver = false;
					continuar = true; // começa de novo
				}
			}
		}
		while (continuar == true);
	}
}
