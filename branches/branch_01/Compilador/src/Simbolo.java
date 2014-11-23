/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
/**
 * 
 */
public class Simbolo {
	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private Classificacao aTipo;
	private String aIdentificador;
	private boolean aMarcadorBloco;

	//~ Construtores ---------------------------------------------------------------------------------------------------------------


	public Simbolo(short pTipo, String pLexema) {
		if ((pTipo == Classificacao.INTEIRO) || (pTipo == Classificacao.REAL) ||
				(pTipo == Classificacao.CARACTER)) {
			this.aTipo = new Classificacao(pTipo);
		}
		this.aIdentificador = pLexema;
	}
	
	public Simbolo(boolean pMarcadorBloco) {
		this.aMarcadorBloco = pMarcadorBloco;
	}


	//~ Metodos --------------------------------------------------------------------------------------------------------------------

	/**
	 * -
	 *
	 * @return
	 */
	public boolean isMarcadorBloco() {
		return this.aMarcadorBloco;
	}
	
	/**
	 * -
	 *
	 * @return
	 */
	public Classificacao getTipo() {
		return this.aTipo;
	}

	/**
	 * -
	 *
	 * @return
	 */
	public String getIdentificador() {
		return this.aIdentificador;
	}
}
