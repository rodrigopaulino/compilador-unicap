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
	private Character aLookAheadAnterior;
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
			case '\n':
				break;
			case '\t':
				break;
			case ' ':
				break;
			case '+':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco)
					return this.aUltimoTokenLido = new Token(Classificacao.SOMA);
				
				break;
			case '-':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco)
					return this.aUltimoTokenLido = new Token(Classificacao.SUBTRACAO);
				
				break;
			case '*':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco)
					return this.aUltimoTokenLido = new Token(Classificacao.MULTIPLICACAO);
				
				break;
			case '(':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco)
					return this.aUltimoTokenLido = new Token(Classificacao.PARENTESES_ABRE);
				
				break;
			case ')':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco)
					return this.aUltimoTokenLido = new Token(Classificacao.PARENTESES_FECHA);
				
				break;
			case '{':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco)
					return this.aUltimoTokenLido = new Token(Classificacao.CHAVE_ABRE);
				
				break;
			case '}':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco)
					return this.aUltimoTokenLido = new Token(Classificacao.CHAVE_FECHA);
				
				break;
			case ',':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco)
					return this.aUltimoTokenLido = new Token(Classificacao.VIRGULA);
				
				break;
			case ';':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco)
					return this.aUltimoTokenLido = new Token(Classificacao.PONTO_VIRGULA);
				
				break;
			case '<':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco) {
					this.lookAhead(pBuffReader);
					
					if (this.aLookAhead == '=')
						return this.aUltimoTokenLido = new Token(Classificacao.MENOR_IGUAL);
					else
						return this.aUltimoTokenLido = new Token(Classificacao.MENOR);
				}
				break;
			case '>':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco) {
					this.lookAhead(pBuffReader);
					
					if (this.aLookAhead == '=')
						return this.aUltimoTokenLido = new Token(Classificacao.MAIOR_IGUAL);
					else
						return this.aUltimoTokenLido = new Token(Classificacao.MAIOR);
				}
				break;
			case '=':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco) {
					this.lookAhead(pBuffReader);
					
					if (this.aLookAhead == '=')
						return this.aUltimoTokenLido = new Token(Classificacao.IGUAL);
					else
						return this.aUltimoTokenLido = new Token(Classificacao.ATRIBUICAO);
				}
				break;
			case '!':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco) {
					this.lookAhead(pBuffReader);
					
					if (this.aLookAhead == '=')
						return this.aUltimoTokenLido = new Token(Classificacao.DIFERENTE);
					else
						throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Operador Relacional Invalido.");
				}
				break;
			case '/':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco) {
					this.lookAhead(pBuffReader);
					
					if (this.aLookAhead == '/')
						this.aIsComentarioLinha = true;
					else if (this.aLookAhead == '*')
						this.aIsComentarioBloco = true;
					else
						return this.aUltimoTokenLido = new Token(Classificacao.DIVISAO);
				}
				break;
			case '\'':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco) {
					lexema += this.aLookAhead.toString();
					
					this.lookAhead(pBuffReader);
					
					if (Character.isDigit(this.aLookAhead) || Character.isLetter(this.aLookAhead)) {
						lexema += this.aLookAhead.toString();
						
						this.lookAhead(pBuffReader);
						
						if (this.aLookAhead == '\'') {
							lexema += this.aLookAhead.toString();
							
							return this.aUltimoTokenLido = new Token(Classificacao.CARACTER, lexema);
						} else {
							throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Caracter Invalido.");
						}
					} else {
						throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Caracter Invalido.");
					}
				}
				break;
			case '.':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco) {
					lexema += this.aLookAhead.toString();
					
					this.lookAhead(pBuffReader);
					
					while(Character.isDigit(this.aLookAhead)){
						lexema += this.aLookAhead.toString();
						
						this.lookAhead(pBuffReader);
					}
					
					if (lexema.charAt(lexema.length() - 1) == '.') {
						throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Numero Decimal Invalido.");
					} else {
						return this.aUltimoTokenLido = new Token(Classificacao.REAL, lexema);
					}
				}
				break;
			case '_':
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco) {
					lexema += this.aLookAhead.toString();
					
					this.aColuna++;
					this.lookAhead(pBuffReader);
					
					while (Character.isDigit(this.aLookAhead) || Character.isLetter(this.aLookAhead) || this.aLookAhead == '_') {
						lexema += this.aLookAhead.toString();
						
						this.aColuna++;
						this.lookAhead(pBuffReader);
					}
					
					return this.aUltimoTokenLido = new Token(Classificacao.ID, lexema);
				}
				break;
			default:
				if (this.isFimArquivo())
					break;
				if (!this.aIsComentarioLinha && !this.aIsComentarioBloco) {
					lexema += this.aLookAhead.toString();
					
					if (Character.isLetter(this.aLookAhead)) {
						this.aColuna++;
						this.lookAhead(pBuffReader);
						
						while(Character.isLetter(this.aLookAhead) || Character.isDigit(this.aLookAhead) || this.aLookAhead == '_'){
							lexema += this.aLookAhead.toString();
							
							this.aColuna++;
							this.lookAhead(pBuffReader);
						}
						
						if (Scanner.aPalavrasReservadas.containsKey(lexema) && (this.aLookAhead == ' ' || this.aLookAhead == '+'
								|| this.aLookAhead == '-' || this.aLookAhead == '*' || this.aLookAhead == '/'
								|| this.aLookAhead == '=' || this.aLookAhead == ')' || this.aLookAhead == '('
								|| this.aLookAhead == '{' || this.aLookAhead == '}' || this.aLookAhead == ','
								|| this.aLookAhead == ';')) {
							return this.aUltimoTokenLido = new Token(Scanner.aPalavrasReservadas.get(lexema), lexema);
						} else if (Scanner.aPalavrasReservadas.containsKey(lexema)) {
							throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Palavra Reservada Nao Delimitada Corretamente.");
						} else {
							return this.aUltimoTokenLido = new Token(Classificacao.ID, lexema);
						}
					} else if (Character.isDigit(this.aLookAhead)) {
						this.aColuna++;
						this.lookAhead(pBuffReader);
						
						while(Character.isDigit(this.aLookAhead)){
							lexema += this.aLookAhead.toString();
							
							this.aColuna++;
							this.lookAhead(pBuffReader);
						}
						
						if (this.aLookAhead != '.') {
							return this.aUltimoTokenLido = new Token(Classificacao.INTEIRO, lexema);
						}
					} else {
						throw new ExcecaoCompilador(this.aLinha, this.aColuna, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getClassificacao().getDescricao():"", "Caracter Nao Reconhecido.");
					}
				}
				break;
			}
		}
		return null;
	}
	
	private void lookAhead(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		int intChar = pBuffReader.read();
		
		if (intChar == -1) {
			this.aInFimArquivo = true;
		} else if (intChar == 13) {
			intChar = pBuffReader.read();
			
			if (intChar != 10) {
				throw new ExcecaoCompilador(this.aLinha, this.aColuna
						, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getLexema():""
							, "CR-LF Invalido.");
			}
			
			this.aColuna = 0;
			this.aLinha++;
		} else if (intChar == 10) {
			throw new ExcecaoCompilador(this.aLinha, this.aColuna
					, (this.aUltimoTokenLido != null)?this.aUltimoTokenLido.getLexema():""
						, "CR-LF Invalido.");
		} else if (intChar == 9) {
			this.aColuna = this.aColuna + 4;
		} else {
			this.aColuna++;
		}
		
		this.aLookAheadAnterior = this.aLookAhead;
		this.aLookAhead = (char) intChar;
	}
	
	public boolean isFimArquivo() {
		return this.aInFimArquivo;
	}
}
