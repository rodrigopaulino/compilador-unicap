/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
package util;

import java.io.BufferedReader;
import java.io.IOException;

import excecoes.ExcecaoCompilador;

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
		this.programa(pBuffReader);
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
							throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
						}
					} else {
						throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
					}
				} else {
					throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
				}
			} else {
				throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
			}
		} else {
			throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
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
			}

			while (this.comando(pBuffReader)) {
			}

			if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.CHAVE_FECHA) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

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
	private boolean comando(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.comandoBasico(pBuffReader)) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.iteracao(pBuffReader)) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.IF) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_ABRE) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoRelacional(pBuffReader)) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_FECHA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.comando(pBuffReader)) {
							this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

							if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ELSE) {
								this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

								if (this.comando(pBuffReader)) {
									this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

									return true;
								} else {
									return false;
								}
							} else {
								return true;
							}
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
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
	private boolean comandoBasico(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.atribuicao(pBuffReader)) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.bloco(pBuffReader)) {
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
	private boolean iteracao(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.WHILE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_ABRE)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoRelacional(pBuffReader)) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_FECHA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.comando(pBuffReader)) {
							this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.DO) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.comando(pBuffReader)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.WHILE) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_ABRE) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.expressaoRelacional(pBuffReader)) {
							this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

							if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_FECHA) {
								this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

								if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PONTO_VIRGULA) {
									this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

									return true;
								} else {
									return false;
								}
							} else {
								return false;
							}
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
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
	private boolean atribuicao(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ID) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ATRIBUICAO)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoAritmetica(pBuffReader)) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PONTO_VIRGULA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
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
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					return true;
				} else {
					return false;
				}
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
	private boolean expressaoAritmetica(BufferedReader pBuffReader)
		throws IOException, ExcecaoCompilador {
		if (this.termo(pBuffReader)) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.expressaoAritmeticaAuxiliar(pBuffReader)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

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
			if (this.termo(pBuffReader)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoAritmeticaAuxiliar(pBuffReader)) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

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
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.termoAuxiliar(pBuffReader)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

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
			if (this.fator(pBuffReader)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.termoAuxiliar(pBuffReader)) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

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
						return false;
					}
				}

				if (this.aLookAhead.getClassificacao().getClassificacao() != Classificacao.PONTO_VIRGULA) {
					return false;
				}
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

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
			return false;
		}
	}
}
