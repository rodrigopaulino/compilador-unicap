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

	private String aMensagemErro;
	private Token aLookAhead;

	//~ Construtores ---------------------------------------------------------------------------------------------------------------

/**
         * Cria um novo objeto Scanner.
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
			throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(), null,
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

		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.INT) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.MAIN) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_ABRE) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_FECHA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (!this.bloco(pBuffReader)) {
							throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
								this.aLookAhead.getLexema(), this.aMensagemErro);
						} else if (!Scanner.getInstancia().isFimArquivo()) {
							throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
								this.aLookAhead.getLexema(), "Programa finalizado antes do fim de arquivo.");
						}
					} else {
						throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
							this.aLookAhead.getLexema(), "Inicio de programa invalido. " + "Fim de parenteses esperado.");
					}
				} else {
					throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
						this.aLookAhead.getLexema(), "Inicio de programa invalido. " + "Inicio de parenteses esperado.");
				}
			} else {
				throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
					this.aLookAhead.getLexema(), "Inicio de programa invalido. " + "Palavra 'main' esperada.");
			}
		} else {
			throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
				this.aLookAhead.getLexema(), "Inicio de programa invalido. " + "Palavra 'int' esperada.");
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
		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.CHAVE_ABRE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			while (this.declaracaoVariavel(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.CHAVE_FECHA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					return true;
				}
			}

			while (this.comando(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.CHAVE_FECHA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					return true;
				}
			}

			this.aMensagemErro = "Bloco invalido. " + "Bloco nao fechado ou mal formado.";

			return false;
		} else {
			this.aMensagemErro = "Bloco invalido. " + "Inicio de chaves esperado.";

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
	private boolean comando(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.comandoBasico(pBuffReader)) {
			return true;
		} else if (this.iteracao(pBuffReader)) {
			return true;
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.IF) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_ABRE) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoRelacional(pBuffReader)) {
					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_FECHA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.comando(pBuffReader)) {
							if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ELSE) {
								this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

								if (this.comando(pBuffReader)) {
									return true;
								} else {
									return false;
								}
							} else {
								this.aMensagemErro = "Comando invalido. " + "Palavra else esperada.";

								return true;
							}
						} else {
							return false;
						}
					} else {
						this.aMensagemErro = "Comando invalido. " + "Fim de parenteses esperado.";

						return false;
					}
				} else {
					return false;
				}
			} else {
				this.aMensagemErro = "Comando invalido. " + "Inicio de parenteses esperado.";

				return false;
			}
		} else {
			this.aMensagemErro = "Comando invalido. " + "Palavra if, ou comando, ou iteracao esperadas.";

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
		} else if (this.bloco(pBuffReader)) {
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
	private boolean iteracao(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.WHILE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_ABRE)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoRelacional(pBuffReader)) {
					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_FECHA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.comando(pBuffReader)) {
							return true;
						} else {
							return false;
						}
					} else {
						this.aMensagemErro = "Iteracao invalida. " + "Fim de parenteses esperado.";

						return false;
					}
				} else {
					return false;
				}
			} else {
				this.aMensagemErro = "Iteracao invalida. " + "Inicio de parenteses esperado.";

				return false;
			}
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.DO) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.comando(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.WHILE) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_ABRE) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.expressaoRelacional(pBuffReader)) {
							if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_FECHA) {
								this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

								if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PONTO_VIRGULA) {
									this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

									return true;
								} else {
									this.aMensagemErro = "Iteracao invalida. " + "Ponto e virgula esperadas.";

									return false;
								}
							} else {
								this.aMensagemErro = "Iteracao invalida. " + "Fim de parenteses esperado.";

								return false;
							}
						} else {
							return false;
						}
					} else {
						this.aMensagemErro = "Iteracao invalida. " + "Inicio de parenteses esperado.";

						return false;
					}
				} else {
					this.aMensagemErro = "Iteracao invalida. " + "Palavra while esperada.";

					return false;
				}
			} else {
				return false;
			}
		} else {
			this.aMensagemErro = "Iteracao invalida. " + "Palavra do/while esperada.";

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
		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ID) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ATRIBUICAO)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoAritmetica(pBuffReader)) {
					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PONTO_VIRGULA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						return true;
					} else {
						this.aMensagemErro = "Atribuicao invalida. " + "Ponto e virgula esperadas.";

						return false;
					}
				} else {
					return false;
				}
			} else {
				this.aMensagemErro = "Atribuicao invalida. " + "Sinal \"=\" esperado.";

				return false;
			}
		} else {
			this.aMensagemErro = "Atribuicao invalida. " + "Identificador esperado.";

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
	private boolean expressaoRelacional(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.expressaoAritmetica(pBuffReader)) {
			if ((this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.IGUAL) ||
					(this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.DIFERENTE) ||
					(this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.MAIOR) ||
					(this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.MAIOR_IGUAL) ||
					(this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.MENOR) ||
					(this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.MENOR_IGUAL)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoAritmetica(pBuffReader)) {
					return true;
				} else {
					return false;
				}
			} else {
				this.aMensagemErro = "Expressao relacional invalida. " +
					"Um dos seguintes operadores esperado: =, !=, >, >=, <, <=.";

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
	private boolean expressaoAritmetica(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.termo(pBuffReader)) {
			if (this.expressaoAritmeticaAuxiliar(pBuffReader)) {
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
	private boolean expressaoAritmeticaAuxiliar(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if ((this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.SOMA) ||
				(this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.SUBTRACAO)) {
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
		if ((this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.MULTIPLICACAO) ||
				(this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.DIVISAO)) {
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
		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_ABRE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.expressaoAritmetica(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_FECHA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					return true;
				} else {
					this.aMensagemErro = "Fator invalido. " + "Fim de parenteses esperado.";

					return false;
				}
			} else {
				return false;
			}
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ID) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.REAL) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.INTEIRO) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.CARACTER) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else {
			this.aMensagemErro = "Fator invalido. " +
				"Identificador, ou real, ou inteiro, caracter, ou expressao aritmetica dentre parenteses esperado.";

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
			if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ID) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				while (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.VIRGULA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ID) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
					} else {
						this.aMensagemErro = "Declaracao de variavel invalida. " + "Identificador esperado.";

						return false;
					}
				}

				if (this.aLookAhead.getClassificacao().getClassificacao() != Classificacao.PONTO_VIRGULA) {
					this.aMensagemErro = "Declaracao de variavel invalida. " + "Ponto e virgula esperadas.";

					return false;
				}
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				return true;
			} else {
				this.aMensagemErro = "Declaracao de variavel invalida. " + "Identificador esperado.";

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
	private boolean tipo(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.INT) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.FLOAT) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.CHAR) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else {
			this.aMensagemErro = "Tipo invalido. " + "Tipos permitidos: int, float e char.";

			return false;
		}
	}
}
