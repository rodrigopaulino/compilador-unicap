/*
 * Este arquivo � propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informa��o nele contida pode ser reproduzida,
 * mostrada ou revelada sem permiss�o escrita do mesmo.
 */
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
	public Token(short pClassificacao, String pLexema) {
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
	public Token(short pClassificacao) {
		this.aClassificacao = new Classificacao(pClassificacao);
		switch (pClassificacao) {
			case Classificacao.MAIN:
				this.aLexema = "main";
				break;

			case Classificacao.IF:
				this.aLexema = "if";
				break;

			case Classificacao.ELSE:
				this.aLexema = "else";
				break;

			case Classificacao.WHILE:
				this.aLexema = "while";
				break;

			case Classificacao.INT:
				this.aLexema = "int";
				break;

			case Classificacao.FOR:
				this.aLexema = "for";
				break;

			case Classificacao.DO:
				this.aLexema = "do";
				break;

			case Classificacao.FLOAT:
				this.aLexema = "float";
				break;

			case Classificacao.CHAR:
				this.aLexema = "char";
				break;

			case Classificacao.MAIOR:
				this.aLexema = ">";
				break;

			case Classificacao.MENOR:
				this.aLexema = "<";
				break;

			case Classificacao.MAIOR_IGUAL:
				this.aLexema = ">=";
				break;

			case Classificacao.MENOR_IGUAL:
				this.aLexema = "<=";
				break;

			case Classificacao.IGUAL:
				this.aLexema = "==";
				break;

			case Classificacao.DIFERENTE:
				this.aLexema = "!=";
				break;

			case Classificacao.SOMA:
				this.aLexema = "+";
				break;

			case Classificacao.SUBTRACAO:
				this.aLexema = "-";
				break;

			case Classificacao.MULTIPLICACAO:
				this.aLexema = "*";
				break;

			case Classificacao.DIVISAO:
				this.aLexema = "/";
				break;

			case Classificacao.ATRIBUICAO:
				this.aLexema = "=";
				break;

			case Classificacao.PARENTESES_ABRE:
				this.aLexema = "(";
				break;

			case Classificacao.PARENTESES_FECHA:
				this.aLexema = ")";
				break;

			case Classificacao.CHAVE_ABRE:
				this.aLexema = "{";
				break;

			case Classificacao.CHAVE_FECHA:
				this.aLexema = "}";
				break;

			case Classificacao.VIRGULA:
				this.aLexema = ",";
				break;

			case Classificacao.PONTO_VIRGULA:
				this.aLexema = ";";
				break;

			default:
				break;
		}
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
