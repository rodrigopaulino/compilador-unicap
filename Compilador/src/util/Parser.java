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
						this.bloco(pBuffReader);
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
	 * @throws ExcecaoCompilador 
	 * @throws IOException 
	 */
	private void bloco(BufferedReader pBuffReader) throws ExcecaoCompilador, IOException {
		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.CHAVE_ABRE) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
			
			while (this.declaracaoVariavel(pBuffReader)) {
				
			}
			
		} else {
			throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
		}
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 */
	private void comando(BufferedReader pBuffReader) {
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 */
	private void comandoBasico(BufferedReader pBuffReader) {
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 */
	private void iteracao(BufferedReader pBuffReader) {
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 */
	private void atribuicao(BufferedReader pBuffReader) {
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 */
	private void expressaoRelacional(BufferedReader pBuffReader) {
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 */
	private void expressaoAritmetica(BufferedReader pBuffReader) {
	}

	/**
	 * -
	 *
	 * @param pBuffReader
	 *
	 * @throws IOException
	 * @throws ExcecaoCompilador
	 */
	private void termo(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		this.termo(pBuffReader);

		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.MULTIPLICACAO) {
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

			this.expressaoAritmetica(pBuffReader);

			if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.PARENTESES_FECHA) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

				return true;
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
	
	private boolean declaracaoVariavel(BufferedReader pBuffReader) throws IOException, ExcecaoCompilador {
		this.tipo(pBuffReader);
		
		if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ID) {
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);

			while (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.VIRGULA) {
				this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
				
				if (this.aLookAhead.getClassificacao().getClassificacao() == Classificacao.ID) {
					this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
				} else {
					throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
				}
			}
			
			if (this.aLookAhead.getClassificacao().getClassificacao() != Classificacao.PONTO_VIRGULA) {
				throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
			}
			this.aLookAhead = Scanner.getInstancia().executar(pBuffReader);
			
			return true;
		} else {
			throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
		}
	}
	
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
			throw new ExcecaoCompilador(0, 0, this.aLookAhead.getLexema(), "Erro do Parser");
		}
	}
}
