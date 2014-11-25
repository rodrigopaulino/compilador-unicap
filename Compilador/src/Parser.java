/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Stack;

/**
 * 
 */
public final class Parser {
	//~ Atributos/inicializadores estaticos ----------------------------------------------------------------------------------------

	private static Parser aInstancia;

	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private Stack<Simbolo> aTabelaSimbolos = new Stack<Simbolo>();
	private Token aLookAhead;
	private int aNT = 0;
	private int aNL = 0;
	private StringBuffer aCodigoIntermediario = new StringBuffer();

	//~ Construtores ---------------------------------------------------------------------------------------------------------------

/**
         * Cria um novo objeto Parser.
         */
	private Parser() {
	}

	//~ Metodos --------------------------------------------------------------------------------------------------------------------

	/**
	 * -
	 *
	 * @return
	 */
	public static Parser getInstancia() {
		if (aInstancia == null) {
			aInstancia = new Parser();
		}

		return aInstancia;
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 *
	 * @throws IOException
	 * @throws ExcecaoCompilador
	 */
	public void executar(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		try {
			this.programa(pBuffReader);
			System.out.println(this.aCodigoIntermediario);
		} catch (NullPointerException e) {
			throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
				(Scanner.getInstancia().getUltimoTokenLido() == null) ? "" : Scanner.getInstancia().getUltimoTokenLido().getLexema(),
				"Fim de Arquivo Inesperado.");
		}
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 *
	 * @throws IOException
	 * @throws ExcecaoCompilador
	 */
	private void programa(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.INT) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MAIN) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.bloco(pBuffReader) && !Scanner.getInstancia().isFimArquivo()) {
							throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
									Scanner.getInstancia().getUltimoTokenLido().getLexema(),
									"Dados escritos fora do escopo do programa.");
						}
					} else {
						throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							Scanner.getInstancia().getUltimoTokenLido().getLexema(),
							"Inicio de programa invalido. " + "Fim de parenteses esperado.");
					}
				} else {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Inicio de programa invalido. " + "Inicio de parenteses esperado.");
				}
			} else {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Inicio de programa invalido. " + "Palavra 'main' esperada.");
			}
		} else {
			throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
				Scanner.getInstancia().getUltimoTokenLido().getLexema(), "Inicio de programa invalido. " +
				"Palavra 'int' esperada.");
		}
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 *
	 * @return
	 *
	 * @throws ExcecaoCompilador
	 * @throws IOException
	 */
	private boolean bloco(BufferedReader pBuffReader) throws ExcecaoCompilador, IOException, ExcecaoSemantico {
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAVE_ABRE) {
			this.iniciarBloco();
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			while (this.declaracaoVariavel(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAVE_FECHA) {
					this.retirarBloco();
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					return true;
				}
			}
		
			while (this.comando(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAVE_FECHA) {
					this.retirarBloco();
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					return true;
				}
			}
			
			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAVE_FECHA) {
				this.retirarBloco();
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				return true;
			}
			
			throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Bloco invalido.");
		} else {
			throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Bloco invalido. " + "Inicio de chaves esperado.");
		}
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
	private boolean comando(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		if (this.comandoBasico(pBuffReader)) {
			return true;
		} else if (this.iteracao(pBuffReader)) {
			return true;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.IF) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
				
				Simbolo exprRel = this.expressaoRelacional(pBuffReader);

				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
					int nTemporarioInicial = this.aNL++;
					this.aCodigoIntermediario.append("IF " + exprRel.getIdentificador() 
							+ " == FALSE GOTO L" + nTemporarioInicial + "\n");
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.comando(pBuffReader)) {
						int nTemporarioFinal = this.aNL++;
						this.aCodigoIntermediario.append("GOTO L" + nTemporarioFinal + ":\n");
						this.aCodigoIntermediario.append("L" + nTemporarioInicial + ":\n");
						if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ELSE) {
							this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

							if (this.comando(pBuffReader)) {
								this.aCodigoIntermediario.append("L" + nTemporarioFinal + ":\n");
								return true;
							} else {
								throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
										Scanner.getInstancia().getUltimoTokenLido().getLexema(),
										"Comando invalido dentro do else.");
							}
						} else {
							this.aCodigoIntermediario.append("L" + nTemporarioFinal + ":\n");
							return true;
						}
					} else {
						throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
								Scanner.getInstancia().getUltimoTokenLido().getLexema(),
								"Comando invalido dentro do if.");
					}
				} else {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							Scanner.getInstancia().getUltimoTokenLido().getLexema(),
							"Comando invalido. " + "Fim de parenteses esperado.");
				}
			} else {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Comando invalido. " + "Inicio de parenteses do if esperado.");
			}
		} else {
			return false;
		}
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
	private boolean comandoBasico(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		if (this.atribuicao(pBuffReader)) {
			return true;
		} 

		try {
			this.bloco(pBuffReader);
		} catch (ExcecaoCompilador e) {
			return false;
		}
		
		return true;
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
	private boolean iteracao(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.WHILE) {
			int nTemporarioInicial = this.aNL++;
			this.aCodigoIntermediario.append("L" + nTemporarioInicial + ":\n");
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
				
				Simbolo exprRel = this.expressaoRelacional(pBuffReader);
				
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
					int nTemporarioFinal = this.aNL++;
					this.aCodigoIntermediario.append("IF " + exprRel.getIdentificador() 
							+ " == FALSE GOTO L" + nTemporarioFinal + "\n");
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.comando(pBuffReader)) {
						this.aCodigoIntermediario.append("GOTO L" + nTemporarioInicial + "\n");
						this.aCodigoIntermediario.append("L" + nTemporarioFinal + ":\n");
						return true;
					} else {
						throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
								Scanner.getInstancia().getUltimoTokenLido().getLexema(),
								"Iteracao invalida. " + "Comando mal formado dentro do while.");
					}
				} else {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							Scanner.getInstancia().getUltimoTokenLido().getLexema(),
							"Iteracao invalida. " + "Fim de parenteses esperado.");
				}
			} else {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Iteracao invalida. " + "Inicio de parenteses esperado.");
			}
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.DO) {
			int nTemporario = this.aNL++;
			this.aCodigoIntermediario.append("L" + nTemporario + ":\n");
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.comando(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.WHILE) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						Simbolo exprRel = this.expressaoRelacional(pBuffReader);
						
						if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
							this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

							if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PONTO_VIRGULA) {
								this.aCodigoIntermediario.append("IF " + exprRel.getIdentificador() 
										+ " == TRUE GOTO L" + nTemporario + "\n");
								this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

								return true;
							} else {
								throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
										Scanner.getInstancia().getUltimoTokenLido().getLexema(),
										"Iteracao invalida. " + "Ponto e virgula esperadas.");
							}
						} else {
							throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
									Scanner.getInstancia().getUltimoTokenLido().getLexema(),
									"Iteracao invalida. " + "Fim de parenteses esperado.");
						}
					} else {
						throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
								Scanner.getInstancia().getUltimoTokenLido().getLexema(),
								"Iteracao invalida. " + "Inicio de parenteses esperado.");
					}
				} else {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							Scanner.getInstancia().getUltimoTokenLido().getLexema(),
							"Iteracao invalida. " + "Palavra while esperada.");
				}
			} else {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Iteracao invalida. " + "Comando esperado apos o do.");
			}
		} else {
			return false;
		}
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
	private boolean atribuicao(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		Simbolo variavel = null;
		Simbolo exprDir = null;
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
			variavel = this.variavelDeclarada(this.aLookAhead.getLexema(), false);
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ATRIBUICAO)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				try {
					exprDir = this.expressaoAritmetica(pBuffReader);
				} catch (ExcecaoCompilador e) {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							Scanner.getInstancia().getUltimoTokenLido().getLexema(),
							"Atribuicao invalida. " +
									"Expressao aritmetica a direita do sinal \"=\" esta mal-formada.");
				}
				
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PONTO_VIRGULA) {
					this.gerarCodigoAtribuicao(variavel, exprDir);
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					return true;
				} else {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							Scanner.getInstancia().getUltimoTokenLido().getLexema(),
							"Atribuicao invalida. " + "Ponto e virgula esperado.");
				}
			} else {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Atribuicao invalida. " + "Sinal \"=\" esperado.");
			}
		} else {
			return false;
		}
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
	private Simbolo expressaoRelacional(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		Simbolo exprAritEsq = null;
		Simbolo exprAritDir = null;
		Token operador = null;
		try {
			exprAritEsq = this.expressaoAritmetica(pBuffReader);
		} catch (ExcecaoCompilador e) {
			throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Expressao relacional invalida. " +
							"Expressao aritmetica a esquerda do operador relacional esta mal-formada.");
		}
		
		if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.IGUAL) ||
				(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.DIFERENTE) ||
				(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MAIOR) ||
				(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MAIOR_IGUAL) ||
				(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MENOR) ||
				(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MENOR_IGUAL)) {
			operador = this.aLookAhead;
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			try {
				exprAritDir = this.expressaoAritmetica(pBuffReader);
			} catch (ExcecaoCompilador e) {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Expressao relacional invalida. " +
								"Expressao aritmetica a direita do operador relacional esta mal-formada.");
			}
			
			return this.gerarCodigoExpressaoRelacional(operador, exprAritEsq, exprAritDir);
		} else {
			throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Expressao relacional invalida. " +
							"Um dos seguintes operadores esperado: ==, !=, >, >=, <, <=.");
		}
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
	private Simbolo expressaoAritmetica(BufferedReader pBuffReader)
		throws ExcecaoCompilador, ExcecaoSemantico, IOException {
		Simbolo termo = this.termo(pBuffReader);
		if (termo != null) {
			Operacao expressaoAritmeticaAuxiliar =  this.expressaoAritmeticaAuxiliar(pBuffReader);
			if (expressaoAritmeticaAuxiliar != null) {
				if (expressaoAritmeticaAuxiliar.isVazio()) {
					return termo;
				} else {
					return this.gerarCodigoExpressaoAritmetica(termo, expressaoAritmeticaAuxiliar);
				}
			} else {
				throw new ExcecaoCompilador();
			}
		} else {
			throw new ExcecaoCompilador();
		}
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
	private Operacao expressaoAritmeticaAuxiliar(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.SOMA) ||
				(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.SUBTRACAO)) {
			Token operador = this.aLookAhead;
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			Simbolo termo = this.termo(pBuffReader);
			if (termo != null) {
				Operacao expressaoAritmeticaAuxiliar =  this.expressaoAritmeticaAuxiliar(pBuffReader);
				if (expressaoAritmeticaAuxiliar != null) {
					if (expressaoAritmeticaAuxiliar.isVazio()) {
						return new Operacao(operador.getClassificacao().getCodigo(), termo);
					} else {
						return new Operacao(operador.getClassificacao().getCodigo()
								, this.gerarCodigoExpressaoAritmetica(termo, expressaoAritmeticaAuxiliar));
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return new Operacao();
		}
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
	private Simbolo termo(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		Simbolo fator = this.fator(pBuffReader);
		if (fator != null) {
			Operacao termoAuxiliar =  this.termoAuxiliar(pBuffReader);
			if (termoAuxiliar != null) {
				if (termoAuxiliar.isVazio()) {
					return fator;
				} else {
					return this.gerarCodigoTermo(fator, termoAuxiliar);
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
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
	private Operacao termoAuxiliar(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MULTIPLICACAO) ||
				(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.DIVISAO)) {
			Token operador = this.aLookAhead;
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			Simbolo fator = this.fator(pBuffReader);
			if (fator != null) {
				Operacao termoAuxiliar =  this.termoAuxiliar(pBuffReader);
				if (termoAuxiliar != null) {
					if (termoAuxiliar.isVazio()) {
						return new Operacao(operador.getClassificacao().getCodigo(), fator);
					} else {
						return new Operacao(operador.getClassificacao().getCodigo()
								, this.gerarCodigoTermo(fator, termoAuxiliar));
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return new Operacao();
		}
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
	private Simbolo fator(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		Simbolo fator = null;
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			try {
				fator = this.expressaoAritmetica(pBuffReader);
			} catch (ExcecaoCompilador e) {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Fator invalido. " + "Expressao aritmetica dentre parenteses invalida.");
			}
			
			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
			} else {
				return null;
			}
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
			fator = this.variavelDeclarada(this.aLookAhead.getLexema(), false);
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.REAL) {
			fator = new Simbolo(Classificacao.FLOAT, this.aLookAhead.getLexema());
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.INTEIRO) {
			fator = new Simbolo(Classificacao.INT, this.aLookAhead.getLexema());
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CARACTER) {
			fator = new Simbolo(Classificacao.CHAR, this.aLookAhead.getLexema());
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
		}
			
		return fator;
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
	private boolean declaracaoVariavel(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador, ExcecaoSemantico {
		Token tipo = this.tipo(pBuffReader);
		if (tipo != null) {
			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
				this.incluirVariavel(new Simbolo(tipo.getClassificacao().getCodigo(), this.aLookAhead.getLexema()));
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				while (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.VIRGULA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
						this.incluirVariavel(new Simbolo(tipo.getClassificacao().getCodigo(), this.aLookAhead.getLexema()));
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
					} else {
						throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
								Scanner.getInstancia().getUltimoTokenLido().getLexema(),
								"Declaracao de variavel invalida. " + "Identificador esperado.");
					}
				}

				if (this.aLookAhead.getClassificacao().getCodigo() != Classificacao.PONTO_VIRGULA) {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							Scanner.getInstancia().getUltimoTokenLido().getLexema(),
							"Declaracao de variavel invalida. " + "Ponto e virgula esperado.");
				}
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				return true;
			} else {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Declaracao de variavel invalida. " + "Identificador esperado.");
			}
		} else {
			return false;
		}
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
	private Token tipo(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		Token tipo = this.aLookAhead;
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.INT) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return tipo;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.FLOAT) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return tipo;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAR) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return tipo;
		} else {
			return null;
		}
	}
	
	/*
	 * 
	 * MÉTODOS DE MANIPULAÇÃO DA TABELA DE SÍMBOLOS
	 * 
	 */

	/**
	 * Método responsável por iniciar a Tabela de Símbolos.
	 */
	private void iniciarBloco() {
		Simbolo inicioBloco = new Simbolo(true);
		this.aTabelaSimbolos.push(inicioBloco);
	}

	/**
	 * Método responsável por retirar o último bloco visitado da Tabela de Símbolos.
	 */
	private void retirarBloco() {
		Simbolo simbolo = this.aTabelaSimbolos.pop();

		while (!simbolo.isMarcadorBloco()) {
			simbolo = this.aTabelaSimbolos.pop();
		}
	}
	
	/**
	 * Método responsável por incluir uma nova variável na Tabela de Símbolos.
	 * 
	 * @param pSimbolo Variável a ser inserida.
	 * @throws ExcecaoSemantico Exceção lançada quando a variável já foi declarada no mesmo escopo.
	 */
	private void incluirVariavel(Simbolo pSimbolo) throws ExcecaoSemantico {
		Simbolo variavelDeclarada = null;
		try {
			variavelDeclarada = this.variavelDeclarada(pSimbolo.getIdentificador(), true);
		} catch (ExcecaoSemantico e) {
			this.aTabelaSimbolos.push(pSimbolo);
		}
		
		if (variavelDeclarada != null) {
			throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
				Scanner.getInstancia().getUltimoTokenLido().getLexema(),
				"Variavel ja declarada no mesmo escopo.");
		}
	}

	/**
	 * Método responsável por verificar se uma variável usada já foi declarada.
	 * 
	 * @param pIdentificador Lexema da variável
	 * @param pBuscarNoMesmoEscopo Indicador para buscar variável somente no mesmo escopo
	 * @return Variável declarada
	 * @throws ExcecaoSemantico Caso não seja encontrada, uma exceção é lançada
	 */
	private Simbolo variavelDeclarada(String pIdentificador, boolean pBuscarNoMesmoEscopo) 
			throws ExcecaoSemantico {
		for (int i = this.aTabelaSimbolos.size() - 1; i >= 0; --i) {
			Simbolo simbolo = this.aTabelaSimbolos.get(i);

			if (pBuscarNoMesmoEscopo && simbolo.isMarcadorBloco()) {
				break;
			} else if (pIdentificador.equals(simbolo.getIdentificador())) {
				return simbolo;
			}
		}
		
		throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
			Scanner.getInstancia().getUltimoTokenLido().getLexema(),
				"Variavel usada nao foi declarada.");
	}

	/*
	 * 
	 * MÉTODOS DE GERAÇÃO DE CÓDIGO
	 * 
	 */
	
	/**
	 * Método responsável por gerar código intermediário das gerações do não-terminal "atribuicao".
	 * Além disso, verifica semanticamente a compatibilidade de tipos.
	 * 
	 * @param pSimboloEsq
	 * @param pSimboloDir
	 * @throws ExcecaoSemantico
	 */
	public void gerarCodigoAtribuicao(Simbolo pSimboloEsq, Simbolo pSimboloDir) throws ExcecaoSemantico {
		/*
		 * Verifica se os tipos envolvidos na operação são iguais, ou compatíveis 
		 * (um FLOAT não pode ser atribuido a um INT)
		 */
		if (pSimboloEsq.getTipo().getCodigo() == pSimboloDir.getTipo().getCodigo()) {
			// Realiza a atribuição
			this.aCodigoIntermediario.append(pSimboloEsq.getIdentificador() + " = " 
					+ pSimboloDir.getIdentificador() + "\n");
		} else if (pSimboloEsq.getTipo().getCodigo() == Classificacao.FLOAT 
				&& pSimboloDir.getTipo().getCodigo() == Classificacao.INT) {
			// Converte o tipo INT para FLOAT
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pSimboloDir.getIdentificador() + ")\n");
			
			// Realiza a atribuição
			this.aCodigoIntermediario.append(pSimboloEsq.getIdentificador() + " = " + "T" 
					+ (this.aNT - 1) + "\n");
		} else {
			throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Atribuicao de tipos incompativeis.");
		}
		
		this.aCodigoIntermediario.append("\n");
	}
	
	/**
	 * Método responsável por gerar código intermediário das gerações do não-terminal "expr_relacional".
	 * Além disso, verifica semanticamente a compatibilidade de tipos.
	 * 
	 * @param pOperador
	 * @param pSimboloEsq
	 * @param pSimboloDir
	 * @return
	 * @throws ExcecaoSemantico
	 */
	public Simbolo gerarCodigoExpressaoRelacional(Token pOperador, Simbolo pSimboloEsq
			, Simbolo pSimboloDir) throws ExcecaoSemantico {
		Simbolo simboloResultante;
		
		// Verifica se os tipos envolvidos na operação são iguais, ou compatíveis (INT e FLOAT)
		if (pSimboloEsq.getTipo().getCodigo() == pSimboloDir.getTipo().getCodigo()) {
			// Realiza a operação relacional
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ pSimboloEsq.getIdentificador() + pOperador.getLexema() 
					+ pSimboloDir.getIdentificador() + "\n");
			
			// Resultado da operação
			simboloResultante = new Simbolo(pSimboloEsq.getTipo().getCodigo(), "T" + (this.aNT - 1));
		} else if (pSimboloEsq.getTipo().getCodigo() == Classificacao.INT 
				&& pSimboloDir.getTipo().getCodigo() == Classificacao.FLOAT){
			// Converte o tipo INT para FLOAT
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pSimboloEsq.getIdentificador() + ")\n");
			
			// Realiza a operação relacional
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
					+ (this.aNT - 2) + pOperador.getLexema() + pSimboloDir.getIdentificador() + "\n");
			
			// Resultado da operação
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else if (pSimboloEsq.getTipo().getCodigo() == Classificacao.FLOAT 
				&& pSimboloDir.getTipo().getCodigo() == Classificacao.INT) {
			// Converte o tipo INT para FLOAT
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pSimboloDir.getIdentificador() + ")\n");
			
			// Realiza a operação relacional
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ pSimboloEsq.getIdentificador() + pOperador.getLexema() + "T" 
					+ (this.aNT - 2) + "\n");
			
			// Resultado da operação
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else {
			throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Expressao relacional com tipos incompativeis.");
		}
		
		this.aCodigoIntermediario.append("\n");
		return simboloResultante;
	}
	
	/**
	 * Método responsável por gerar código intermediário das gerações do não-terminal "expr_arit".
	 * Além disso, verifica semanticamente a compatibilidade de tipos.
	 * 
	 * @param pSimbolo
	 * @param pOperacao
	 * @return
	 * @throws ExcecaoSemantico
	 */
	public Simbolo gerarCodigoExpressaoAritmetica(Simbolo pSimbolo, Operacao pOperacao) throws ExcecaoSemantico {
		Simbolo simboloResultante;
		
		// Verifica se os tipos envolvidos na operação são iguais, ou compatíveis (INT e FLOAT)
		if (pSimbolo.getTipo().getCodigo() == pOperacao.getSimbolo().getTipo().getCodigo()) {
			if (pOperacao.getOperacao().getCodigo() == Classificacao.SOMA) {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "+" 
						+ pOperacao.getSimbolo().getIdentificador() + "\n");
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "-" 
						+ pOperacao.getSimbolo().getIdentificador() + "\n");
			}
			simboloResultante = new Simbolo(pSimbolo.getTipo().getCodigo(), "T" + (this.aNT - 1));
		} else if (pSimbolo.getTipo().getCodigo() == Classificacao.INT 
				&& pOperacao.getSimbolo().getTipo().getCodigo() == Classificacao.FLOAT){
			// Converte o tipo INT para FLOAT
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pSimbolo.getIdentificador() + ")\n");
			
			// Realiza a soma ou subtração
			if (pOperacao.getOperacao().getCodigo() == Classificacao.SOMA) {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
						+ (this.aNT - 2) + "+" + pOperacao.getSimbolo().getIdentificador() + "\n");
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
						+ (this.aNT - 2) + "-" + pOperacao.getSimbolo().getIdentificador() + "\n");
			}
			
			// Resultado da operação
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else if (pSimbolo.getTipo().getCodigo() == Classificacao.FLOAT 
				&& pOperacao.getSimbolo().getTipo().getCodigo() == Classificacao.INT) {
			// Converte o tipo INT para FLOAT
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pOperacao.getSimbolo().getIdentificador() + ")\n");
			
			// Realiza a soma ou subtração
			if (pOperacao.getOperacao().getCodigo() == Classificacao.SOMA) {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "+T" + (this.aNT - 2) + "\n");
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "-T" + (this.aNT - 2) + "\n");
			}
			
			// Resultado da operação
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else {
			throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Expressao aritmetica com tipos incompativeis.");
		}
		
		this.aCodigoIntermediario.append("\n");
		return simboloResultante;
	}
	
	/**
	 * Método responsável por gerar código intermediário das gerações do não-terminal "termo".
	 * Além disso, verifica semanticamente a compatibilidade de tipos.
	 * 
	 * @param pSimbolo
	 * @param pOperacao
	 * @return
	 * @throws ExcecaoSemantico
	 */
	public Simbolo gerarCodigoTermo(Simbolo pSimbolo, Operacao pOperacao) throws ExcecaoSemantico {
		Simbolo simboloResultante;
		
		// Verifica se os tipos envolvidos na operação são iguais, ou compatíveis (INT e FLOAT)
		if (pSimbolo.getTipo().getCodigo() == pOperacao.getSimbolo().getTipo().getCodigo()) {
			
			// Caso seja uma divisão e os tipos sejam INT, transformar os dois em FLOAT
			if (pOperacao.getOperacao().getCodigo() == Classificacao.DIVISAO) {
				if (pSimbolo.getTipo().getCodigo() == Classificacao.INT) {
					// Primeiramente, converte os INTs em FLOATs
					this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
							+ "i2f(" + pOperacao.getSimbolo().getIdentificador() + ")\n");
					this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
							+ "i2f(" + pSimbolo.getIdentificador() + ")\n");
					
					// Realiza a divisão de dois FLOATs
					this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
							+ (this.aNT - 2) + "/T" + (this.aNT - 3) + "\n");
					
					// Resultado da operação
					simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
				} else {
					// Realiza a divisão de tipos iguais
					this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
							+ pSimbolo.getIdentificador() + "/" 
							+ pOperacao.getSimbolo().getIdentificador() + "\n");
					
					// Resultado da operação
					simboloResultante = new Simbolo(pSimbolo.getTipo().getCodigo(), "T" + (this.aNT - 1));
				}
			} else {
				// Realiza a multiplicação de tipos iguais
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "*" 
						+ pOperacao.getSimbolo().getIdentificador() + "\n");
				
				// Resultado da operação
				simboloResultante = new Simbolo(pSimbolo.getTipo().getCodigo(), "T" + (this.aNT - 1));
			}
		} else if (pSimbolo.getTipo().getCodigo() == Classificacao.INT 
				&& pOperacao.getSimbolo().getTipo().getCodigo() == Classificacao.FLOAT){
			// Converte o tipo INT para FLOAT
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pSimbolo.getIdentificador() + ")\n");
			
			// Realiza a divisão ou multiplicação
			if (pOperacao.getOperacao().getCodigo() == Classificacao.DIVISAO) {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
						+ (this.aNT - 2) + "/" + pOperacao.getSimbolo().getIdentificador() + "\n");
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
						+ (this.aNT - 2) + "*" + pOperacao.getSimbolo().getIdentificador() + "\n");
			}
			
			// Resultado da operação
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else if (pSimbolo.getTipo().getCodigo() == Classificacao.FLOAT 
				&& pOperacao.getSimbolo().getTipo().getCodigo() == Classificacao.INT) {
			// Converte o tipo INT para FLOAT
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pOperacao.getSimbolo().getIdentificador() + ")\n");
			
			// Realiza a divisão ou multiplicação
			if (pOperacao.getOperacao().getCodigo() == Classificacao.DIVISAO) {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "/T" + (this.aNT - 2) + "\n");
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "*T" + (this.aNT - 2) + "\n");
			}
			
			// Resultado da operação
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else {
			throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Expressao aritmetica com tipos incompativeis.");
		}
		
		this.aCodigoIntermediario.append("\n");
		return simboloResultante;
	}
}