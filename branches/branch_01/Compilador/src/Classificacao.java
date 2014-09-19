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

	private short aCodigo;

	//~ Construtores ---------------------------------------------------------------------------------------------------------------

/**
         * Cria um novo objeto Classificacao.
         *
         * @param pCodigo  
         *
         * @throws ExcecaoClassificacaoInexistente  
         */
	public Classificacao(short pCodigo) {
		if ((pCodigo == Classificacao.ID) || (pCodigo == Classificacao.INTEIRO) || (pCodigo == Classificacao.REAL) ||
				(pCodigo == Classificacao.CARACTER) || (pCodigo == Classificacao.MAIN) || (pCodigo == Classificacao.IF) ||
				(pCodigo == Classificacao.ELSE) || (pCodigo == Classificacao.WHILE) || (pCodigo == Classificacao.DO) ||
				(pCodigo == Classificacao.FOR) || (pCodigo == Classificacao.INT) || (pCodigo == Classificacao.FLOAT) ||
				(pCodigo == Classificacao.CHAR) || (pCodigo == Classificacao.MAIOR) || (pCodigo == Classificacao.MENOR) ||
				(pCodigo == Classificacao.MAIOR_IGUAL) || (pCodigo == Classificacao.MENOR_IGUAL) ||
				(pCodigo == Classificacao.IGUAL) || (pCodigo == Classificacao.DIFERENTE) || (pCodigo == Classificacao.SOMA) ||
				(pCodigo == Classificacao.SUBTRACAO) || (pCodigo == Classificacao.MULTIPLICACAO) ||
				(pCodigo == Classificacao.DIVISAO) || (pCodigo == Classificacao.ATRIBUICAO) ||
				(pCodigo == Classificacao.PARENTESES_ABRE) || (pCodigo == Classificacao.PARENTESES_FECHA) ||
				(pCodigo == Classificacao.CHAVE_ABRE) || (pCodigo == Classificacao.CHAVE_FECHA) ||
				(pCodigo == Classificacao.VIRGULA) || (pCodigo == Classificacao.PONTO_VIRGULA)) {
			this.aCodigo = pCodigo;
		}
	}

	//~ Metodos --------------------------------------------------------------------------------------------------------------------

	/**
	 * -
	 *
	 * @return
	 */
	public short getCodigo() {
		return this.aCodigo;
	}

	/**
	 * -
	 *
	 * @return
	 */
	public String getDescricao() {
		switch (this.aCodigo) {
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
