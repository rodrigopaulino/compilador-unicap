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
	private Operacao aOperacao;

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
	 */
	private void iniciarBloco() {
		Simbolo inicioBloco = new Simbolo(true);
		this.aTabelaSimbolos.push(inicioBloco);
	}

	/**
	 * -
	 */
	private void retirarBloco() {
		Simbolo simbolo = this.aTabelaSimbolos.pop();

		while (!simbolo.isMarcadorBloco()) {
			simbolo = this.aTabelaSimbolos.pop();
		}
	}
	
	/**
	 * -
	 */
	private void incluirVariavel(Simbolo pSimbolo) throws ExcecaoSemantico {
		Simbolo variavelDeclarada = this.variavelDeclarada(pSimbolo.getIdentificador(), true);

		if (variavelDeclarada == null) {
			this.aTabelaSimbolos.push(pSimbolo);
		} else {
			throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Variavel ja declarada no mesmo escopo.");
		}
	}

	/**
	 * -
	 */
	private Simbolo variavelDeclarada(String pIdentificador, boolean pBuscarNoMesmoEscopo) {
		for (int i = this.aTabelaSimbolos.size() - 1; i >= 0; --i) {
			Simbolo simbolo = this.aTabelaSimbolos.get(i);

			if (pBuscarNoMesmoEscopo && simbolo.isMarcadorBloco()) {
				break;
			} else if (pIdentificador.equals(simbolo.getIdentificador())) {
				return simbolo;
			}
		}

		return null;
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
				
				this.expressaoRelacional(pBuffReader);

				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.comando(pBuffReader)) {
						if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ELSE) {
							this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

							if (this.comando(pBuffReader)) {
								return true;
							} else {
								throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
										Scanner.getInstancia().getUltimoTokenLido().getLexema(),
										"Comando invalido dentro do else.");
							}
						} else {
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
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
				
				this.expressaoRelacional(pBuffReader);
				
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.comando(pBuffReader)) {
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
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.comando(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.WHILE) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						this.expressaoRelacional(pBuffReader);
						
						if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
							this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

							if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PONTO_VIRGULA) {
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
		Simbolo tipo1 = null;
		Simbolo tipo2 = null;
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
			Simbolo variavelDeclarada = this.variavelDeclarada(this.aLookAhead.getLexema(), false);
			if (variavelDeclarada == null) {
				throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Variavel usada nao foi declarada.");
			}
			tipo1 = new Simbolo(variavelDeclarada.getTipo().getCodigo(), null);
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ATRIBUICAO)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				try {
					tipo2 = this.expressaoAritmetica(pBuffReader);
				} catch (ExcecaoCompilador e) {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							Scanner.getInstancia().getUltimoTokenLido().getLexema(),
							"Atribuicao invalida. " +
									"Expressao aritmetica a direita do sinal \"=\" esta mal-formada.");
				}
				
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PONTO_VIRGULA) {
					if ((tipo1.getTipo().getCodigo() != tipo2.getTipo().getCodigo()) &&
							!((tipo1.getTipo().getCodigo() == Classificacao.INT 
									&& tipo2.getTipo().getCodigo() == Classificacao.FLOAT) ||
									(tipo1.getTipo().getCodigo() == Classificacao.FLOAT 
									&& tipo2.getTipo().getCodigo() == Classificacao.INT))) {
						throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
								Scanner.getInstancia().getUltimoTokenLido().getLexema(),
								"Atribuicao de tipos incompativeis.");
					}
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
		Simbolo tipo1 = null;
		Simbolo tipo2 = null;
		Token operador = null;
		try {
			tipo1 = this.expressaoAritmetica(pBuffReader);
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
				tipo2 = this.expressaoAritmetica(pBuffReader);
				return this.gerarCodigoExpressaorRelacional(operador, tipo1, tipo2);
			} catch (ExcecaoCompilador e) {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Expressao relacional invalida. " +
								"Expressao aritmetica a direita do operador relacional esta mal-formada.");
			}
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
		Simbolo tipo;
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			try {
				tipo = this.expressaoAritmetica(pBuffReader);
			} catch (ExcecaoCompilador e) {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Fator invalido. " + "Expressao aritmetica dentre parenteses invalida.");
			}
			
			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				return tipo;
			} else {
				return null;
			}
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
			Simbolo variavelDeclarada = this.variavelDeclarada(this.aLookAhead.getLexema(), false);
			if (variavelDeclarada == null) {
				throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Variavel usada nao foi declarada.");
			}
			tipo = variavelDeclarada;
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return tipo;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.REAL) {
			tipo = new Simbolo(Classificacao.FLOAT, this.aLookAhead.getLexema());
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return tipo;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.INTEIRO) {
			tipo = new Simbolo(Classificacao.INT, this.aLookAhead.getLexema());
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return tipo;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CARACTER) {
			tipo = new Simbolo(Classificacao.CHAR, this.aLookAhead.getLexema());
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return tipo;
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
	 * MÉTODOS DE GERAÇÃO DE CÓDIGO
	 * 
	 */
	
	public Simbolo gerarCodigoExpressaorRelacional(Token pOperador, Simbolo pSimboloEsq
			, Simbolo pSimboloDir) throws ExcecaoSemantico {
		Simbolo simboloResultante;
		if (pSimboloEsq.getTipo().getCodigo() == pSimboloDir.getTipo().getCodigo()) {
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ pSimboloEsq.getIdentificador() + pOperador.getLexema() 
					+ pSimboloDir.getIdentificador() + "\n");
			simboloResultante = new Simbolo(pSimboloEsq.getTipo().getCodigo(), "T" + (this.aNT - 1));
		} else if (pSimboloEsq.getTipo().getCodigo() == Classificacao.INT 
				&& pSimboloDir.getTipo().getCodigo() == Classificacao.FLOAT){
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pSimboloEsq.getIdentificador() + ")\n");
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
					+ (this.aNT - 2) + pOperador.getLexema() + pSimboloDir.getIdentificador() + "\n");
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else if (pSimboloEsq.getTipo().getCodigo() == Classificacao.FLOAT 
				&& pSimboloDir.getTipo().getCodigo() == Classificacao.INT) {
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pSimboloDir.getIdentificador() + ")\n");
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ pSimboloEsq.getIdentificador() + pOperador.getLexema() + "T" 
					+ (this.aNT - 2) + "\n");
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else {
			throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Expressao relacional com tipos incompativeis.");
		}
		return simboloResultante;
	}
	
	public Simbolo gerarCodigoExpressaoAritmetica(Simbolo pSimbolo, Operacao pOperacao) throws ExcecaoSemantico {
		Simbolo simboloResultante;
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
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pSimbolo.getIdentificador() + ")\n");
			if (pOperacao.getOperacao().getCodigo() == Classificacao.SOMA) {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
						+ (this.aNT - 2) + "+" + pOperacao.getSimbolo().getIdentificador() + "\n");
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
						+ (this.aNT - 2) + "-" + pOperacao.getSimbolo().getIdentificador() + "\n");
			}
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else if (pSimbolo.getTipo().getCodigo() == Classificacao.FLOAT 
				&& pOperacao.getSimbolo().getTipo().getCodigo() == Classificacao.INT) {
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pOperacao.getSimbolo().getIdentificador() + ")\n");
			if (pOperacao.getOperacao().getCodigo() == Classificacao.SOMA) {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "+T" + (this.aNT - 2) + "\n");
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "-T" + (this.aNT - 2) + "\n");
			}
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else {
			throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Expressao aritmetica com tipos incompativeis.");
		}
		return simboloResultante;
	}
	
	public Simbolo gerarCodigoTermo(Simbolo pSimbolo, Operacao pOperacao) throws ExcecaoSemantico {
		Simbolo simboloResultante;
		if (pSimbolo.getTipo().getCodigo() == pOperacao.getSimbolo().getTipo().getCodigo()) {
			if (pOperacao.getOperacao().getCodigo() == Classificacao.DIVISAO) {
				if (pSimbolo.getTipo().getCodigo() == Classificacao.INT) {
					this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
							+ "i2f(" + pOperacao.getSimbolo().getIdentificador() + ")\n");
					this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
							+ "i2f(" + pSimbolo.getIdentificador() + ")\n");
					this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
							+ (this.aNT - 2) + "/T" + (this.aNT - 3) + "\n");
					simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
				} else {
					this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
							+ pSimbolo.getIdentificador() + "/" 
							+ pOperacao.getSimbolo().getIdentificador() + "\n");
					simboloResultante = new Simbolo(pSimbolo.getTipo().getCodigo(), "T" + (this.aNT - 1));
				}
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "*" 
						+ pOperacao.getSimbolo().getIdentificador() + "\n");
				simboloResultante = new Simbolo(pSimbolo.getTipo().getCodigo(), "T" + (this.aNT - 1));
			}
		} else if (pSimbolo.getTipo().getCodigo() == Classificacao.INT 
				&& pOperacao.getSimbolo().getTipo().getCodigo() == Classificacao.FLOAT){
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pSimbolo.getIdentificador() + ")\n");
			if (pOperacao.getOperacao().getCodigo() == Classificacao.DIVISAO) {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
						+ (this.aNT - 2) + "/" + pOperacao.getSimbolo().getIdentificador() + "\n");
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = T" 
						+ (this.aNT - 2) + "*" + pOperacao.getSimbolo().getIdentificador() + "\n");
			}
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else if (pSimbolo.getTipo().getCodigo() == Classificacao.FLOAT 
				&& pOperacao.getSimbolo().getTipo().getCodigo() == Classificacao.INT) {
			this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
					+ "i2f(" + pOperacao.getSimbolo().getIdentificador() + ")\n");
			if (pOperacao.getOperacao().getCodigo() == Classificacao.DIVISAO) {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "/T" + (this.aNT - 2) + "\n");
			} else {
				this.aCodigoIntermediario.append("T" + this.aNT++ + " = " 
						+ pSimbolo.getIdentificador() + "*T" + (this.aNT - 2) + "\n");
			}
			simboloResultante = new Simbolo(Classificacao.FLOAT, "T" + (this.aNT - 1));
		} else {
			throw new ExcecaoSemantico(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					Scanner.getInstancia().getUltimoTokenLido().getLexema(),
					"Expressao aritmetica com tipos incompativeis.");
		}
		return simboloResultante;
	}
}