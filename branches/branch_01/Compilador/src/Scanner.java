/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 */
public final class Scanner {
	//~ Atributos/inicializadores estaticos ----------------------------------------------------------------------------------------

	private static Scanner aInstancia;
	private static HashMap<String, Short> aPalavrasReservadas;

	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private Character aLookAhead;
	private Token aUltimoTokenLido;
	private boolean aInFimArquivo = false;
	private int aColuna = 0;
	private int aLinha = 1;

	//~ Construtores ---------------------------------------------------------------------------------------------------------------

/**
         * Cria um novo objeto Scanner.
         */
	private Scanner() {
		Scanner.aPalavrasReservadas = new HashMap<String, Short>();
		Scanner.aPalavrasReservadas.put("main", Classificacao.MAIN);
		Scanner.aPalavrasReservadas.put("if", Classificacao.IF);
		Scanner.aPalavrasReservadas.put("else", Classificacao.ELSE);
		Scanner.aPalavrasReservadas.put("while", Classificacao.WHILE);
		Scanner.aPalavrasReservadas.put("do", Classificacao.DO);
		Scanner.aPalavrasReservadas.put("for", Classificacao.FOR);
		Scanner.aPalavrasReservadas.put("int", Classificacao.INT);
		Scanner.aPalavrasReservadas.put("float", Classificacao.FLOAT);
		Scanner.aPalavrasReservadas.put("char", Classificacao.CHAR);
	}

	//~ Metodos --------------------------------------------------------------------------------------------------------------------

	/**
	 * -
	 *
	 * @return
	 */
	public boolean isInFimArquivo() {
		return aInFimArquivo;
	}

	/**
	 * -
	 *
	 * @return
	 */
	public Token getUltimoTokenLido() {
		return aUltimoTokenLido;
	}

	/**
	 * -
	 *
	 * @return
	 */
	public int getColuna() {
		return aColuna;
	}

	/**
	 * -
	 *
	 * @return
	 */
	public int getLinha() {
		return aLinha;
	}

	/**
	 * -
	 *
	 * @return
	 */
	public static Scanner getInstancia() {
		if (aInstancia == null) {
			aInstancia = new Scanner();
		}

		return aInstancia;
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 *
	 * @return
	 *
	 * @throws IOException
	 * @throws ExcecaoCompilador
	 */
	public Token executar(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		String lexema = "";

		while (!this.aInFimArquivo) {
			while ((this.aLookAhead == null) || Character.isWhitespace(this.aLookAhead)) {
				this.lookAhead(pBuffReader);
			}

			switch (this.aLookAhead) {
				case '+':
					this.lookAhead(pBuffReader);

					return this.aUltimoTokenLido = new Token(Classificacao.SOMA);

				case '-':
					this.lookAhead(pBuffReader);

					return this.aUltimoTokenLido = new Token(Classificacao.SUBTRACAO);

				case '*':
					this.lookAhead(pBuffReader);

					return this.aUltimoTokenLido = new Token(Classificacao.MULTIPLICACAO);

				case '(':
					this.lookAhead(pBuffReader);

					return this.aUltimoTokenLido = new Token(Classificacao.PARENTESES_ABRE);

				case ')':
					this.lookAhead(pBuffReader);

					return this.aUltimoTokenLido = new Token(Classificacao.PARENTESES_FECHA);

				case '{':
					this.lookAhead(pBuffReader);

					return this.aUltimoTokenLido = new Token(Classificacao.CHAVE_ABRE);

				case '}':
					this.lookAhead(pBuffReader);

					return this.aUltimoTokenLido = new Token(Classificacao.CHAVE_FECHA);

				case ',':
					this.lookAhead(pBuffReader);

					return this.aUltimoTokenLido = new Token(Classificacao.VIRGULA);

				case ';':
					this.lookAhead(pBuffReader);

					return this.aUltimoTokenLido = new Token(Classificacao.PONTO_VIRGULA);

				case '!':
					this.lookAhead(pBuffReader);

					if (this.aLookAhead == '=') {
						this.lookAhead(pBuffReader);

						return this.aUltimoTokenLido = new Token(Classificacao.DIFERENTE);
					} else {
						throw new ExcecaoCompilador(this.aLinha, this.aColuna,
							(this.aUltimoTokenLido != null) ? this.aUltimoTokenLido.getLexema() : "",
							"Operador Relacional Invalido.");
					}

				case '<':
					this.lookAhead(pBuffReader);

					if (this.aLookAhead == '=') {
						this.lookAhead(pBuffReader);

						return this.aUltimoTokenLido = new Token(Classificacao.MENOR_IGUAL);
					} else {
						return this.aUltimoTokenLido = new Token(Classificacao.MENOR);
					}

				case '>':
					this.lookAhead(pBuffReader);

					if (this.aLookAhead == '=') {
						this.lookAhead(pBuffReader);

						return this.aUltimoTokenLido = new Token(Classificacao.MAIOR_IGUAL);
					} else {
						return this.aUltimoTokenLido = new Token(Classificacao.MAIOR);
					}

				case '=':
					this.lookAhead(pBuffReader);

					if (this.aLookAhead == '=') {
						this.lookAhead(pBuffReader);

						return this.aUltimoTokenLido = new Token(Classificacao.IGUAL);
					} else {
						return this.aUltimoTokenLido = new Token(Classificacao.ATRIBUICAO);
					}

				case '\'':
					lexema += this.aLookAhead.toString();
					this.lookAhead(pBuffReader);

					if (Character.isDigit(this.aLookAhead) || Character.isLetter(this.aLookAhead)) {
						lexema += this.aLookAhead.toString();

						this.lookAhead(pBuffReader);

						if (this.aLookAhead == '\'') {
							lexema += this.aLookAhead.toString();

							this.lookAhead(pBuffReader);

							return this.aUltimoTokenLido = new Token(Classificacao.CARACTER, lexema);
						} else {
							throw new ExcecaoCompilador(this.aLinha, this.aColuna,
								(this.aUltimoTokenLido != null) ? this.aUltimoTokenLido.getLexema() : "",
								"Token do Tipo Caracter Invalido.");
						}
					} else {
						throw new ExcecaoCompilador(this.aLinha, this.aColuna,
							(this.aUltimoTokenLido != null) ? this.aUltimoTokenLido.getLexema() : "",
							"Token do Tipo Caracter Invalido.");
					}

				case '.':
					lexema += this.aLookAhead.toString();
					this.lookAhead(pBuffReader);

					while (Character.isDigit(this.aLookAhead)) {
						lexema += this.aLookAhead.toString();

						this.lookAhead(pBuffReader);
					}

					if (lexema.charAt(lexema.length() - 1) == '.') {
						throw new ExcecaoCompilador(this.aLinha, this.aColuna,
							(this.aUltimoTokenLido != null) ? this.aUltimoTokenLido.getLexema() : "", "Numero Decimal Invalido.");
					} else {
						return this.aUltimoTokenLido = new Token(Classificacao.REAL, lexema);
					}

				case '_':
					lexema += this.aLookAhead.toString();
					this.lookAhead(pBuffReader);

					while (Character.isDigit(this.aLookAhead) || Character.isLetter(this.aLookAhead) || (this.aLookAhead == '_')) {
						lexema += this.aLookAhead.toString();

						this.lookAhead(pBuffReader);
					}

					return this.aUltimoTokenLido = new Token(Classificacao.ID, lexema);

				case '/':
					this.lookAhead(pBuffReader);

					if (this.aLookAhead == '/') {
						this.lookAhead(pBuffReader);

						while (this.aLookAhead != '\n') {
							this.lookAhead(pBuffReader);
						}
					} else if (this.aLookAhead == '*') {
						while (true) {
							this.lookAhead(pBuffReader);

							if (this.aInFimArquivo) {
								throw new ExcecaoCompilador(this.aLinha, this.aColuna,
									(this.aUltimoTokenLido != null) ? this.aUltimoTokenLido.getLexema() : "",
									"Fim de Arquivo Antes de Fim de Comentario.");
							}

							if (this.aLookAhead == '*') {
								this.lookAhead(pBuffReader);

								while (this.aLookAhead == '*') {
									this.lookAhead(pBuffReader);
								}

								if (this.aInFimArquivo) {
									throw new ExcecaoCompilador(this.aLinha, this.aColuna,
										(this.aUltimoTokenLido != null) ? this.aUltimoTokenLido.getLexema() : "",
										"Fim de Arquivo Antes de Fim de Comentario.");
								}

								if (this.aLookAhead == '/') {
									this.lookAhead(pBuffReader);

									break;
								}
							}
						}
					} else {
						return this.aUltimoTokenLido = new Token(Classificacao.DIVISAO);
					}

					break;

				default:

					if (this.aInFimArquivo) {
						break;
					}
					lexema += this.aLookAhead.toString();

					if (Character.isLetter(this.aLookAhead)) {
						this.lookAhead(pBuffReader);

						while (Character.isLetter(this.aLookAhead) || Character.isDigit(this.aLookAhead) ||
								(this.aLookAhead == '_')) {
							lexema += this.aLookAhead.toString();

							this.lookAhead(pBuffReader);
						}

						if (Scanner.aPalavrasReservadas.containsKey(lexema)) {
							return this.aUltimoTokenLido = new Token(Scanner.aPalavrasReservadas.get(lexema));
						} else {
							return this.aUltimoTokenLido = new Token(Classificacao.ID, lexema);
						}
					} else if (Character.isDigit(this.aLookAhead)) {
						this.lookAhead(pBuffReader);

						while (Character.isDigit(this.aLookAhead)) {
							lexema += this.aLookAhead.toString();

							this.lookAhead(pBuffReader);
						}

						if (this.aLookAhead != '.') {
							return this.aUltimoTokenLido = new Token(Classificacao.INTEIRO, lexema);
						}
					} else {
						throw new ExcecaoCompilador(this.aLinha, this.aColuna,
							(this.aUltimoTokenLido != null) ? this.aUltimoTokenLido.getLexema() : "", "Caracter Nao Reconhecido.");
					}
			}
		}

		return null;
	}

	/**
	 * -
	 *
	 * @return
	 */
	public boolean isFimArquivo() {
		return aInFimArquivo;
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 *
	 * @throws IOException
	 * @throws ExcecaoCompilador
	 */
	private void lookAhead(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		int intChar = pBuffReader.read();

		if (intChar == -1) {
			this.aInFimArquivo = true;
		} else if (intChar == 13) { // Para sistemas Windows onde a quebra de linha acontece com CR+LF
			intChar = pBuffReader.read();

			if (intChar != 10) {
				throw new ExcecaoCompilador(this.aLinha, this.aColuna,
					(this.aUltimoTokenLido != null) ? this.aUltimoTokenLido.getLexema() : "", "CR-LF Invalido.");
			}

			this.aColuna = 0;
			this.aLinha++;
		} else if (intChar == 10) { // Para sistemas LINUX onde a quebra de linha acontece só com LF
			this.aColuna = 0;
			this.aLinha++;
		} else if (intChar == 9) {
			this.aColuna = this.aColuna + 4;
		} else {
			this.aColuna++;
		}

		this.aLookAhead = (char) intChar;
	}
}
