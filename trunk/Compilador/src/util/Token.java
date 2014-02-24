package util;

import excecoes.ExcecaoClassificacaoInexistente;

public class Token {
	
	private Classificacao aClassificacao;
	private String aLexema;
	
	public Token(short pClassificacao, String pLexema) throws ExcecaoClassificacaoInexistente{
		this.aClassificacao = new Classificacao(pClassificacao);
		this.aLexema = pLexema;
	}
	
	public Token(short pClassificacao) throws ExcecaoClassificacaoInexistente{
		this.aClassificacao = new Classificacao(pClassificacao);
	}
	
	public short getClassificacao() {
		return this.aClassificacao.getClassificacao();
	}
	
	public String getLexema() {
		return this.aLexema;
	}
}
