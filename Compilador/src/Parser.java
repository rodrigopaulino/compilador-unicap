/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
import java.io.BufferedReader;
import java.io.IOException;

/**
 * 
 */
public final class Parser {
	//~ Atributos/inicializadores estaticos ----------------------------------------------------------------------------------------

	private static Parser aInstancia;

	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private Token aLookAhead;

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
	public void executar(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		try {
			this.programa(pBuffReader);
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
	private void programa(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
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
	private boolean bloco(BufferedReader pBuffReader) throws ExcecaoCompilador, IOException {
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAVE_ABRE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			while (this.declaracaoVariavel(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAVE_FECHA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					return true;
				}
			}
		
			while (this.comando(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAVE_FECHA) {
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
		throws IOException, ExcecaoCompilador {
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
		throws IOException, ExcecaoCompilador {
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
		throws IOException, ExcecaoCompilador {
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
		throws IOException, ExcecaoCompilador {
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ATRIBUICAO)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				try {
					this.expressaoAritmetica(pBuffReader);
				} catch (Exception e) {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							Scanner.getInstancia().getUltimoTokenLido().getLexema(),
							"Atribuicao invalida. " +
									"Expressao aritmetica a direita do sinal \"=\" esta mal-formada.");
				}
				
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PONTO_VIRGULA) {
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
	private void expressaoRelacional(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		try {
			this.expressaoAritmetica(pBuffReader);
		} catch (Exception e) {
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
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			try {
				this.expressaoAritmetica(pBuffReader);
			} catch (Exception e) {
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
	private void expressaoAritmetica(BufferedReader pBuffReader)
		throws Exception {
		if (this.termo(pBuffReader)) {
			if (!this.expressaoAritmeticaAuxiliar(pBuffReader)) {
				throw new Exception();
			}
		} else {
			throw new Exception();
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
	private boolean expressaoAritmeticaAuxiliar(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.SOMA) ||
				(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.SUBTRACAO)) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.termo(pBuffReader)) {
				if (this.expressaoAritmeticaAuxiliar(pBuffReader)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return true;
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
	private boolean termo(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		if (this.fator(pBuffReader)) {
			if (this.termoAuxiliar(pBuffReader)) {
				return true;
			} else {
				return false;
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
	private boolean termoAuxiliar(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MULTIPLICACAO) ||
				(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.DIVISAO)) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.fator(pBuffReader)) {
				if (this.termoAuxiliar(pBuffReader)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return true;
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
	private boolean fator(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			try {
				this.expressaoAritmetica(pBuffReader);
			} catch (Exception e) {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						Scanner.getInstancia().getUltimoTokenLido().getLexema(),
						"Fator invalido. " + "Expressao aritmetica dentre parenteses invalida.");
			}
			
			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				return true;
			} else {
				return false;
			}
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.REAL) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.INTEIRO) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CARACTER) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
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
	private boolean declaracaoVariavel(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.tipo(pBuffReader)) {
			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				while (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.VIRGULA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID) {
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
	private boolean tipo(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.INT) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.FLOAT) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAR) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else {
			return false;
		}
	}
}
