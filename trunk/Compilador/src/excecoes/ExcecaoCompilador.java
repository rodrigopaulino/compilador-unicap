package excecoes;

public class ExcecaoCompilador extends Exception {
	
	private String aMessagem;
	
	public ExcecaoCompilador(int pLinha, int pColuna, String pUltimoToken, String pErro) {
		super();
		this.aMessagem = "ERRO na linha " + pLinha + ", coluna " + pColuna + ", ultimo token lido \"" + pUltimoToken + "\":" + pErro;
	}
	
	public String getMessage() {
		return this.aMessagem;
	}

}
