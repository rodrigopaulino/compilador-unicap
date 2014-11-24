/*
 * Este arquivo � propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informa��o nele contida pode ser reproduzida,
 * mostrada ou revelada sem permiss�o escrita do mesmo.
 */
/**
 * Tipo auxiliar na verifica��o sem�ntica e gera��o de c�digo
 * intermedi�rio de Termos e Express�es Aritm�ticas.
 */
public class Operacao {
	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private Classificacao aOperacao;
	private Simbolo aSimbolo;
	private boolean aVazio;

	//~ Construtores ---------------------------------------------------------------------------------------------------------------


	public Operacao(short pOperacao, Simbolo pSimbolo) {
		this.aOperacao = new Classificacao(pOperacao);
		this.aSimbolo = pSimbolo;
	}
	
	public Operacao() {
		this.aVazio = true;
	}

	//~ Metodos --------------------------------------------------------------------------------------------------------------------

	/**
	 * -
	 *
	 * @return
	 */
	public boolean isVazio() {
		return this.aVazio;
	}
	
	/**
	 * -
	 *
	 * @return
	 */
	public Classificacao getOperacao() {
		return this.aOperacao;
	}
	
	/**
	 * -
	 *
	 * @return
	 */
	public Simbolo getSimbolo() {
		return this.aSimbolo;
	}
}
