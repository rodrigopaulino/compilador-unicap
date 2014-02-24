package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import excecoes.ExcecaoClassificacaoInexistente;
import excecoes.ExcecaoCompilador;

public final class Scanner {
	private static Scanner aInstancia;
	private int aColuna = 0;
	private int aLinha = 1;
	private Token aUltimoTokenLido;
	private Character aLookAhead;
	private boolean aIsComentarioLinha = false;
	private boolean aIsComentarioBloco = false;
	private boolean aInFimArquivo = false;
	
	private static HashMap<String, Short> aPalavrasReservadas;
	
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
	
	public static Scanner getInstancia() {
		if (aInstancia == null) {
			aInstancia = new Scanner();
		}
		
		return aInstancia;
	}
	
	public Token executar(BufferedReader pBuffReader) throws IOException, ExcecaoClassificacaoInexistente, ExcecaoCompilador{
		String lexema = "";
		
		while (!this.aInFimArquivo) {
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
					throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Operador Relacional Invalido.");
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
						throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Caracter Invalido.");
					}
				} else {
					throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Caracter Invalido.");
				}
			case '.':
				lexema += this.aLookAhead.toString();
				
				this.lookAhead(pBuffReader);
				
				while (Character.isDigit(this.aLookAhead)) {
					lexema += this.aLookAhead.toString();
					
					this.lookAhead(pBuffReader);
				}
				
				if (lexema.charAt(lexema.length() - 1) == '.') {
					throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Numero Decimal Invalido.");
				} else {
					return this.aUltimoTokenLido = new Token(Classificacao.REAL, lexema);
				}
			case '_':
					lexema += this.aLookAhead.toString();
					
					this.lookAhead(pBuffReader);
					
					while (Character.isDigit(this.aLookAhead) || Character.isLetter(this.aLookAhead) || this.aLookAhead == '_') {
						lexema += this.aLookAhead.toString();
						
						this.lookAhead(pBuffReader);
					}
					
					return this.aUltimoTokenLido = new Token(Classificacao.ID, lexema);
			case '/':
					this.lookAhead(pBuffReader);
					
					if (this.aLookAhead == '/') {
						this.aIsComentarioLinha = true;
						
						while (this.aIsComentarioLinha) {
							this.lookAhead(pBuffReader);
						}
					} else if (this.aLookAhead == '*') {
						while (true) {
							this.lookAhead(pBuffReader);
							
							if (this.aLookAhead == '*') {
								this.lookAhead(pBuffReader);
								
								while (this.aLookAhead == '*') {
									this.lookAhead(pBuffReader);
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
			default:
				lexema += this.aLookAhead.toString();
				
				if (Character.isLetter(this.aLookAhead)) {
					this.lookAhead(pBuffReader);
					
					while (Character.isLetter(this.aLookAhead) || Character.isDigit(this.aLookAhead) || this.aLookAhead == '_') {
						lexema += this.aLookAhead.toString();
						
						this.lookAhead(pBuffReader);
					}
					
					if (Scanner.aPalavrasReservadas.containsKey(lexema)) {
						return this.aUltimoTokenLido = new Token(Scanner.aPalavrasReservadas.get(lexema), lexema);
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
					throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Caracter Nao Reconhecido.");
				}
			}
			
		}
		
		return null;
	}
	
	private void lookAhead(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		int intChar = pBuffReader.read();
		
		if (intChar == -1) {
			this.aInFimArquivo = true;
			return;
		} else if (intChar == 13) {
			intChar = pBuffReader.read();
			
			if (intChar != 10) {
				throw new ExcecaoCompilador(this.aLinha, this.aColuna
						, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getLexema():""
							, "CR-LF Invalido.");
			}
			
			this.aIsComentarioLinha = false;
			this.aColuna = 0;
			this.aLinha++;
			this.lookAhead(pBuffReader);
			return;
		} else if (intChar == 10) {
			throw new ExcecaoCompilador(this.aLinha, this.aColuna
					, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getLexema():""
						, "CR-LF Invalido.");
		} else if (intChar == 9) {
			this.aColuna = this.aColuna + 4;
			this.lookAhead(pBuffReader);
			return;
		} else {
			this.aColuna++;
			this.aLookAhead = (char) intChar;
		}
	}
	
	public boolean isFimArquivo() {
		return this.aInFimArquivo;
	}
}
