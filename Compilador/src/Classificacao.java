/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */


/**
 * 
 */
public class Classificacao {
	//~ Atributos/inicializadores estaticos ----------------------------------------------------------------------------------------

	/**
	 * -
	 */
	public static final short ID = 0;

	/**
	 * -
	 */
	public static final short INTEIRO = 1;

	/**
	 * -
	 */
	public static final short REAL = 2;

	/**
	 * -
	 */
	public static final short CARACTER = 3;

	/**
	 * -
	 */
	public static final short MAIN = 4;

	/**
	 * -
	 */
	public static final short IF = 5;

	/**
	 * -
	 */
	public static final short ELSE = 6;

	/**
	 * -
	 */
	public static final short WHILE = 7;

	/**
	 * -
	 */
	public static final short DO = 8;

	/**
	 * -
	 */
	public static final short FOR = 9;

	/**
	 * -
	 */
	public static final short INT = 10;

	/**
	 * -
	 */
	public static final short FLOAT = 11;

	/**
	 * -
	 */
	public static final short CHAR = 12;

	/**
	 * -
	 */
	public static final short MAIOR = 13;

	/**
	 * -
	 */
	public static final short MENOR = 14;

	/**
	 * -
	 */
	public static final short MAIOR_IGUAL = 15;

	/**
	 * -
	 */
	public static final short MENOR_IGUAL = 16;

	/**
	 * -
	 */
	public static final short IGUAL = 17;

	/**
	 * -
	 */
	public static final short DIFERENTE = 18;

	/**
	 * -
	 */
	public static final short SOMA = 19;

	/**
	 * -
	 */
	public static final short SUBTRACAO = 20;

	/**
	 * -
	 */
	public static final short MULTIPLICACAO = 21;

	/**
	 * -
	 */
	public static final short DIVISAO = 22;

	/**
	 * -
	 */
	public static final short ATRIBUICAO = 23;

	/**
	 * -
	 */
	public static final short PARENTESES_ABRE = 24;

	/**
	 * -
	 */
	public static final short PARENTESES_FECHA = 25;

	/**
	 * -
	 */
	public static final short CHAVE_ABRE = 26;

	/**
	 * -
	 */
	public static final short CHAVE_FECHA = 27;

	/**
	 * -
	 */
	public static final short VIRGULA = 28;

	/**
	 * -
	 */
	public static final short PONTO_VIRGULA = 29;

	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private short aClassificacao;

	//~ Construtores ---------------------------------------------------------------------------------------------------------------

/**
         * Cria um novo objeto Classificacao.
         *
         * @param pClassificacao  
         *
         * @throws ExcecaoClassificacaoInexistente  
         */
	public Classificacao(short pClassificacao) {
		if ((pClassificacao == Classificacao.ID) || (pClassificacao == Classificacao.INTEIRO) ||
				(pClassificacao == Classificacao.REAL) || (pClassificacao == Classificacao.CARACTER) ||
				(pClassificacao == Classificacao.MAIN) || (pClassificacao == Classificacao.IF) ||
				(pClassificacao == Classificacao.ELSE) || (pClassificacao == Classificacao.WHILE) ||
				(pClassificacao == Classificacao.DO) || (pClassificacao == Classificacao.FOR) ||
				(pClassificacao == Classificacao.INT) || (pClassificacao == Classificacao.FLOAT) ||
				(pClassificacao == Classificacao.CHAR) || (pClassificacao == Classificacao.MAIOR) ||
				(pClassificacao == Classificacao.MENOR) || (pClassificacao == Classificacao.MAIOR_IGUAL) ||
				(pClassificacao == Classificacao.MENOR_IGUAL) || (pClassificacao == Classificacao.IGUAL) ||
				(pClassificacao == Classificacao.DIFERENTE) || (pClassificacao == Classificacao.SOMA) ||
				(pClassificacao == Classificacao.SUBTRACAO) || (pClassificacao == Classificacao.MULTIPLICACAO) ||
				(pClassificacao == Classificacao.DIVISAO) || (pClassificacao == Classificacao.ATRIBUICAO) ||
				(pClassificacao == Classificacao.PARENTESES_ABRE) || (pClassificacao == Classificacao.PARENTESES_FECHA) ||
				(pClassificacao == Classificacao.CHAVE_ABRE) || (pClassificacao == Classificacao.CHAVE_FECHA) ||
				(pClassificacao == Classificacao.VIRGULA) || (pClassificacao == Classificacao.PONTO_VIRGULA)) {
			this.aClassificacao = pClassificacao;
		}
	}

	//~ Metodos --------------------------------------------------------------------------------------------------------------------

	/**
	 * -
	 *
	 * @return
	 */
	public short getClassificacao() {
		return this.aClassificacao;
	}

	/**
	 * -
	 *
	 * @return
	 */
	public String getDescricao() {
		switch (this.aClassificacao) {
			case Classificacao.ID:
				return "IDENTIFICADOR";

			case Classificacao.INTEIRO:
				return "VALOR INTEIRO";

			case Classificacao.REAL:
				return "VALOR REAL";

			case Classificacao.CARACTER:
				return "CARACTER";

			case Classificacao.MAIN:
				return "MAIN";

			case Classificacao.IF:
				return "IF";

			case Classificacao.ELSE:
				return "ELSE";

			case Classificacao.WHILE:
				return "WHILE";

			case Classificacao.INT:
				return "INT";

			case Classificacao.FOR:
				return "FOR";

			case Classificacao.DO:
				return "DO";

			case Classificacao.FLOAT:
				return "FLOAT";

			case Classificacao.CHAR:
				return "CHAR";

			case Classificacao.MAIOR:
				return "MAIOR";

			case Classificacao.MENOR:
				return "MENOR";

			case Classificacao.MAIOR_IGUAL:
				return "MAIOR OU IGUAL";

			case Classificacao.MENOR_IGUAL:
				return "MENOR OU IGUAL";

			case Classificacao.IGUAL:
				return "IGUAL";

			case Classificacao.DIFERENTE:
				return "DIFERENTE";

			case Classificacao.SOMA:
				return "SOMA";

			case Classificacao.SUBTRACAO:
				return "SUBTRACAO";

			case Classificacao.MULTIPLICACAO:
				return "MULTIPLICACAO";

			case Classificacao.DIVISAO:
				return "DIVISAO";

			case Classificacao.ATRIBUICAO:
				return "ATRIBUICAO";

			case Classificacao.PARENTESES_ABRE:
				return "ABRE PARENTESES";

			case Classificacao.PARENTESES_FECHA:
				return "FECHA PARENTESES";

			case Classificacao.CHAVE_ABRE:
				return "ABRE CHAVES";

			case Classificacao.CHAVE_FECHA:
				return "FECHA CHAVES";

			case Classificacao.VIRGULA:
				return "VIRGULA";

			case Classificacao.PONTO_VIRGULA:
				return "PONTO E VIRGULA";

			default:
				return null;
		}
	}
}
