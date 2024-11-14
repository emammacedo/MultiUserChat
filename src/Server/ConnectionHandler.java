package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

//Classe que define o thread que deverá tratar da comunicação com um cliente
public class ConnectionHandler extends Thread
{
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;

	CanalChat channel;
	ArrayList<Utilizador> utilizadores;
	ArrayList<Mensagem> mensagens;

	public ConnectionHandler(Socket sock, CanalChat channel, ArrayList<Utilizador> utilizadores,
			ArrayList<Mensagem> mensagens)
	{
		this.clientSocket = sock;
		this.channel = channel;
		this.utilizadores = utilizadores;
		this.mensagens = mensagens;

		try
		{
			// Criar canais de comunicação para o socket do cliente
			this.in = new DataInputStream(clientSocket.getInputStream()); // receber dados
			this.out = new DataOutputStream(clientSocket.getOutputStream()); // enviar dados
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() // c.start executa o método run()
	{
		try
		{
			boolean continuar = true;
			do
			{
				boolean login = false; // Validade do login

				String username;
				String password;
				String resposta_cliente;
				String clientMsg; // resposta recebida (enviada pelo cliente)

				Utilizador client_atual = null;

				do
				{
					boolean user_n = false;

					System.out.println("- SERVIDOR: A PEDIR UTILIZADOR");
					resposta_cliente = this.in.readUTF();
					System.out.println("- CLIENTE: " + resposta_cliente);
					username = resposta_cliente.split(":")[1];

					System.out.println("- SERVIDOR: A PEDIR PASSWORD");
					resposta_cliente = this.in.readUTF();
					System.out.println("- CLIENTE: " + resposta_cliente);
					password = resposta_cliente.split(":")[1];

					if (!password.equals(channel.getPassword())) // verifica a password
					{
						this.out.writeUTF("WRONG PASS");
						this.out.flush();
						System.out.println("- SERVIDOR: WRONG PASS");
					}
					else
					{
						this.out.writeUTF("CORRECT PASS");
						this.out.flush();
						System.out.println("- SERVIDOR: CORRECT PASS");

						if (utilizadores.size() > 0) // se existirem utilizadores em uso
						{
							for (int i = 0; i < utilizadores.size(); i++) // percorre os utilizadores
							{
								if (username.equals(utilizadores.get(i).getUsername())) // se o username estiver em uso
								{
									if (utilizadores.get(i).isLigado() == true) // utilizador já está ligado
									{
										this.out.writeUTF("ALREADY LOGGED");
										this.out.flush();
										System.out.println("- SERVIDOR: ALREADY LOGGED");
									}
									else
									{
										this.out.writeUTF("OLD USER");
										this.out.flush();
										System.out.println("- SERVIDOR: OLD USER");
										client_atual = utilizadores.get(i);
										client_atual.setLigado(true);
										login = true;
									}
									user_n = true;
								}
							}
						}

						if (user_n == false) // se não estiver em uso
						{
							this.out.writeUTF("NEW USER");
							this.out.flush();
							System.out.println("- SERVIDOR: NEW USER");
							client_atual = new Utilizador(username);
							utilizadores.add(client_atual);
							login = true;
						}

					}

				}
				while (login == false);

				if (login == true)
				{
					System.out.println("- SERVIDOR: LOGIN VALIDO");

					// Mostrar mensagens NÃO LIDAS
					if (mensagens.size() > 0 && client_atual.getLast_message() != mensagens.size())
					{
						this.out.writeUTF("SHOW");
						this.out.flush();
						System.out.println("- SERVIDOR: SHOW");

						for (int i = client_atual.getLast_message() + 1; i < mensagens.size(); i++)
						{
							if (!mensagens.get(i).getUsername().equals(client_atual.getUsername()))
							{
								this.out.writeUTF(mensagens.get(i).getUsername() + ":>" + mensagens.get(i).getSms());
								this.out.flush();
							}
						}
						this.out.writeUTF("FINISH");
						this.out.flush();
						System.out.println("- SERVIDOR: FINISH");

						client_atual.setLast_message(mensagens.size() - 1);
					}
					else
					{
						this.out.writeUTF("NOTHING TO SHOW");
						this.out.flush();
						System.out.println("- SERVIDOR: NOTHING TO SHOW");
					}

					do // Continua para o chat
					{

						clientMsg = this.in.readUTF();

						if (clientMsg.equals("#U"))
						{
							System.out.println("- CLIENTE: REQUEST USERS LIST, " + username);

							for (int i = 0; i < utilizadores.size(); i++) // percorre os utilizadores
							{
								if (utilizadores.get(i).isLigado() == true) // utilizadores ligados
								{
									System.out.println("passo lista utilizadores");
									this.out.writeUTF("User " + utilizadores.get(i).getUsername() + " has sent "
											+ utilizadores.get(i).getN_sms() + " messages.");
								}
							}
							this.out.writeUTF("FINISH");
							this.out.flush();
							System.out.println("- SERVIDOR: FINISH");

						}
						else if (clientMsg.equals("#K"))
						{
							System.out.println("- CLIENTE: REQUEST NUM UNREAD, " + username);

							int n_unread = 0;
							for (int i = client_atual.getLast_message() + 1; i < mensagens.size(); i++)
							{
								if (!mensagens.get(i).getUsername().equals(client_atual.getUsername()))
									n_unread = n_unread + 1;
							}

							// enviar para o cliente
							this.out.writeInt(n_unread);
							this.out.flush();
							System.out.println(" - SERVIDOR: UNREAD MESSAGES = " + n_unread);

						}
						else if (clientMsg.equals("#R"))
						{
							System.out.println("- CLIENTE: REFRESH CHAT, " + username);

							for (int i = client_atual.getLast_message() + 1; i < mensagens.size(); i++)
							{
								if (!mensagens.get(i).getUsername().equals(client_atual.getUsername()))
								{
									this.out.writeUTF(
											mensagens.get(i).getUsername() + ":> " + mensagens.get(i).getSms());
									this.out.flush();
								}
							}
							this.out.writeUTF("FINISH");
							this.out.flush();
							System.out.println("- SERVIDOR: FINISH");

							client_atual.setLast_message(mensagens.size() - 1);

						}
						else if (clientMsg.equals("#H"))
						{
							System.out.println("- CLIENTE: HISTORY, " + username);

							this.out.writeInt(mensagens.size());
							this.out.flush();

							for (int i = 0; i < mensagens.size(); i++)
							{
								this.out.writeUTF(mensagens.get(i).getUsername() + ":> " + mensagens.get(i).getSms());
								this.out.flush();
							}
						}
						else // cliente enviou mensagem para o chat
						{
							System.out.println("- CLIENTE: MESSAGE FROM " + username);

							this.out.writeUTF("MSG OK");
							this.out.flush();

							client_atual.setN_sms(client_atual.getN_sms() + 1); // atualização nº sms enviados
							Mensagem mens = new Mensagem(clientMsg, username);
							mensagens.add(mens);
						}

					}
					while (!clientMsg.toLowerCase().equals("exit"));// Executar enquanto mensagem do cliente seja
																	// diferente de "exit"

				}
				System.out.println("- CLIENTE: EXIT, " + username);
				this.out.writeUTF("EXIT");
				this.out.flush();

				System.out.println("- SERVIDOR: A VERIFICAR SAÍDA");
				String resposta = this.in.readUTF();

				client_atual.setLigado(false);

				if (resposta.equals("C"))
				{
					System.out.println("- CLIENTE: LOGOUT");
					this.out.writeUTF("LOGOUT");
					this.out.flush();
					// Fechar canais de comunicação e socket com o cliente
					this.in.close();
					this.out.close();
					this.clientSocket.close();
					continuar = false;

				}
				else
				{
					System.out.println("- CLIENTE: CONTINUE");
					this.out.writeUTF("CONTINUE");
					this.out.flush();
					login = false;
					// começa de novo
					continuar = true;
				}
			}
			while (continuar == true);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
