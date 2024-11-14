package Server;

public class Utilizador
{
	private String username;
	private int n_sms; // nº mensagens enviadas
	private boolean ligado; // está ligado ao canal ou não
	private int last_message; // indice da última mensagem lida

	public Utilizador()
	{
	}

	public Utilizador(String username)
	{
		this.username = username;
		this.n_sms = 0;
		this.ligado = true;
		this.last_message = -1;
	}

	public int getLast_message()
	{
		return last_message;
	}

	public void setLast_message(int last_message)
	{
		this.last_message = last_message;
	}

	public boolean isLigado()
	{
		return ligado;
	}

	public void setLigado(boolean ligado)
	{
		this.ligado = ligado;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public int getN_sms()
	{
		return n_sms;
	}

	public void setN_sms(int n_sms)
	{
		this.n_sms = n_sms;
	}
}
