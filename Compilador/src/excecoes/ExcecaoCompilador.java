/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
package excecoes;

/**
 * 
 */
public class ExcecaoCompilador extends Exception {
	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private String aMessagem;

	//~ Construtores ---------------------------------------------------------------------------------------------------------------

/**
         * Cria um novo objeto ExcecaoCompilador.
         *
         * @param pLinha  
         * @param pColuna  
         * @param pUltimoToken  
         * @param pErro  
         */
	public ExcecaoCompilador(int pLinha, int pColuna, String pUltimoToken, String pErro) {
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
