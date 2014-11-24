/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
/**
 * Tipo auxiliar na verificação semântica e geração de código
 * intermediário de Termos e Expressões Aritméticas.
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
