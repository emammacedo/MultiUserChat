package Server;

public class Mensagem
{
	private String sms;
	private String username; 

	public Mensagem() 
	{
	}

	public Mensagem(String sms, String username)
	{
		// inicializar atributos do objeto
		this.sms = sms;
		this.username = username;
	}

	public String getSms()
	{
		return sms;
	}

	public void setSms(String sms)
	{
		this.sms = sms;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

}
