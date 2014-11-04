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
	private static final short NOVO_ESCOPO = -1;

	//~ Atributos de instancia -----------------------------------------------------------------------------------------------------

	private String aMensagemErro = "";
	private Token aLookAhead;
	private Token aTokenPreInclusaoTabela;
	private Stack<Token> aTabelaSimbolos = new Stack<Token>(); 

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
			System.out.print(true);
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

						if (!this.bloco(pBuffReader)) {
							throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
								Scanner.getInstancia().getUltimoTokenLido().getLexema(), this.aMensagemErro);
						} else if (!Scanner.getInstancia().isFimArquivo()) {
							throw new ExcecaoCompilador(Scanner.getInstancia().getLinha(), Scanner.getInstancia().getColuna(),
								Scanner.getInstancia().getUltimoTokenLido().getLexema(),
								"Programa finalizado antes do fim de arquivo.");
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
	
	public void iniciarBloco(){
		Token inicioBloco = new Token(Parser.NOVO_ESCOPO);
		this.aTabelaSimbolos.push(inicioBloco);
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
			this.iniciarBloco();
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

			atualizarMensagemErro("Bloco invalido. " + "Bloco nao fechado ou mal formado.");

			return false;
		} else {
			atualizarMensagemErro("Bloco invalido. " + "Inicio de chaves esperado.");

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
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.IF) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoRelacional(pBuffReader)) {
					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.comando(pBuffReader)) {
							if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ELSE) {
								this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

								if (this.comando(pBuffReader)) {
									return true;
								} else {
									return false;
								}
							} else {
								atualizarMensagemErro("Comando invalido. " + "Palavra else esperada.");

								return true;
							}
						} else {
							return false;
						}
					} else {
						atualizarMensagemErro("Comando invalido. " + "Fim de parenteses esperado.");

						return false;
					}
				} else {
					return false;
				}
			} else {
				atualizarMensagemErro("Comando invalido. " + "Inicio de parenteses esperado.");

				return false;
			}
		} else {
			atualizarMensagemErro("Comando invalido. " + "Palavra if, ou comando, ou iteracao esperadas.");

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
		if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.WHILE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoRelacional(pBuffReader)) {
					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.comando(pBuffReader)) {
							return true;
						} else {
							return false;
						}
					} else {
						atualizarMensagemErro("Iteracao invalida. " + "Fim de parenteses esperado.");

						return false;
					}
				} else {
					return false;
				}
			} else {
				atualizarMensagemErro("Iteracao invalida. " + "Inicio de parenteses esperado.");

				return false;
			}
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.DO) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			if (this.comando(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.WHILE) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_ABRE) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						if (this.expressaoRelacional(pBuffReader)) {
							if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
								this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

								if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PONTO_VIRGULA) {
									this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

									return true;
								} else {
									atualizarMensagemErro("Iteracao invalida. " + "Ponto e virgula esperadas.");

									return false;
								}
							} else {
								atualizarMensagemErro("Iteracao invalida. " + "Fim de parenteses esperado.");

								return false;
							}
						} else {
							return false;
						}
					} else {
						atualizarMensagemErro("Iteracao invalida. " + "Inicio de parenteses esperado.");

						return false;
					}
				} else {
					atualizarMensagemErro("Iteracao invalida. " + "Palavra while esperada.");

					return false;
				}
			} else {
				return false;
			}
		} else {
			atualizarMensagemErro("Iteracao invalida. " + "Palavra do/while esperada.");

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

				if (this.expressaoAritmetica(pBuffReader)) {
					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PONTO_VIRGULA) {
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

						return true;
					} else {
						atualizarMensagemErro("Atribuicao invalida. " + "Ponto e virgula esperadas.");

						return false;
					}
				} else {
					return false;
				}
			} else {
				atualizarMensagemErro("Atribuicao invalida. " + "Sinal \"=\" esperado.");

				return false;
			}
		} else {
			atualizarMensagemErro("Atribuicao invalida. " + "Identificador esperado.");

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
			if ((this.aLookAhead.getClassificacao().getCodigo() == Classificacao.IGUAL) ||
					(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.DIFERENTE) ||
					(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MAIOR) ||
					(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MAIOR_IGUAL) ||
					(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MENOR) ||
					(this.aLookAhead.getClassificacao().getCodigo() == Classificacao.MENOR_IGUAL)) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				if (this.expressaoAritmetica(pBuffReader)) {
					return true;
				} else {
					return false;
				}
			} else {
				atualizarMensagemErro("Expressao relacional invalida. " +
					"Um dos seguintes operadores esperado: =, !=, >, >=, <, <=.");

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

			if (this.expressaoAritmetica(pBuffReader)) {
				if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.PARENTESES_FECHA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					return true;
				} else {
					atualizarMensagemErro("Fator invalido. " + "Fim de parenteses esperado.");

					return false;
				}
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
			atualizarMensagemErro("Fator invalido. " +
				"Identificador, ou real, ou inteiro, caracter, ou expressao aritmetica dentre parenteses esperado.");

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
			if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID 
					&& this.incluirVariavel(this.aLookAhead.getLexema())) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				while (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.VIRGULA) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

					if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.ID 
							&& this.incluirVariavel(this.aLookAhead.getLexema())) {
						
						this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
					} else {
						zerarTokenPreInclusao();
						atualizarMensagemErro("Declaracao de variavel invalida. " + "Identificador esperado.");

						return false;
					}
				}

				if (this.aLookAhead.getClassificacao().getCodigo() != Classificacao.PONTO_VIRGULA) {
					zerarTokenPreInclusao();
					atualizarMensagemErro("Declaracao de variavel invalida. " + "Ponto e virgula esperadas.");

					return false;
				}
				zerarTokenPreInclusao();
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				return true;
			} else {
				zerarTokenPreInclusao();
				atualizarMensagemErro("Declaracao de variavel invalida. " + "Identificador esperado.");

				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean incluirVariavel(String pIdentificador){
		boolean retorno = isVariavelUnica(pIdentificador);
		if (retorno) {
			Token variavelDeclarada = new Token(this.aTokenPreInclusaoTabela.getClassificacao().getCodigo(), pIdentificador);
			this.aTabelaSimbolos.push(variavelDeclarada);
		} else {
			atualizarMensagemErro("Variavel ja declarada neste escopo.");
		}
		
		return retorno;	
	}
	
	private boolean isVariavelUnica(String pIdentificador){
		return true;
	}
	
	private void zerarTokenPreInclusao(){
		this.aTokenPreInclusaoTabela = null;
	}
	
	private void iniciarTokenPreInclusao(short pTipo){
		this.aTokenPreInclusaoTabela = new Token(Classificacao.INT);
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
			 iniciarTokenPreInclusao(Classificacao.INT);
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.FLOAT) {
			 iniciarTokenPreInclusao(Classificacao.FLOAT);
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else if (this.aLookAhead.getClassificacao().getCodigo() == Classificacao.CHAR) {
			 iniciarTokenPreInclusao(Classificacao.CHAR);
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			return true;
		} else {
			atualizarMensagemErro("Tipo invalido. " + "Tipos permitidos: int, float e char.");

			return false;
		}
	}

	/**
	 * - Atualiza a mensagem de erro caso seja o primeiro erro encontrado.
	 *
	 * @param pMensagem
	 */
	private void atualizarMensagemErro(String pMensagem) {
		if (this.aMensagemErro.equals("")) {
			this.aMensagemErro = pMensagem;
		}
	}
}
