/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
/**
 * 
 */
public class ExcecaoSemantico extends Exception {
	//~ Atributos/inicializadores estaticos ----------------------------------------------------------------------------------------
	
	private static final long serialVersionUID = -3579697654163187538L;
	
	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private String aMessagem;

	//~ Construtores ---------------------------------------------------------------------------------------------------------------

/**
         * Cria um novo objeto ExcecaoSemantico.
         *
         * @param pLinha  
         * @param pColuna  
         * @param pUltimoToken  
         * @param pErro  
         */
	public ExcecaoSemantico(int pLinha, int pColuna, String pUltimoToken, String pErro) {
		super();
		this.aMessagem = "ERRO na linha " + pLinha + ", coluna " + pColuna + ", ultimo token lido \"" + pUltimoToken + "\":" +
			pErro;
	}

	//~ Metodos --------------------------------------------------------------------------------------------------------------------

	/**
	 * -
	 *
	 * @return
	 */
	public String getMessage() {
		return this.aMessagem;
	}
}
