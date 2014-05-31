/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
package util;

import excecoes.ExcecaoClassificacaoInexistente;

/**
 * 
 */
public class Token {
	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private Classificacao aClassificacao;
	private String aLexema;

	//~ Construtores ---------------------------------------------------------------------------------------------------------------

/**
         * Cria um novo objeto Token.
         *
         * @param pClassificacao  
         * @param pLexema  
         *
         * @throws ExcecaoClassificacaoInexistente  
         */
	public Token(short pClassificacao, String pLexema)
		throws ExcecaoClassificacaoInexistente {
		this.aClassificacao = new Classificacao(pClassificacao);
		this.aLexema = pLexema;
	}

/**
         * Cria um novo objeto Token.
         *
         * @param pClassificacao  
         *
         * @throws ExcecaoClassificacaoInexistente  
         */
	public Token(short pClassificacao) throws ExcecaoClassificacaoInexistente {
		this.aClassificacao = new Classificacao(pClassificacao);
	}

	//~ Metodos --------------------------------------------------------------------------------------------------------------------

	/**
	 * -
	 *
	 * @return
	 */
	public Classificacao getClassificacao() {
		return this.aClassificacao;
	}

	/**
	 * -
	 *
	 * @return
	 */
	public String getLexema() {
		return this.aLexema;
	}
}
