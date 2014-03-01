/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
package principal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import excecoes.ExcecaoClassificacaoInexistente;
import excecoes.ExcecaoCompilador;

import util.Scanner;
import util.Token;

/**
 * 
 */
public class Compilador {
	//~ Metodos --------------------------------------------------------------------------------------------------------------------

	/**
	 * DOCUMENT ME!
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		FileReader fReader;
		BufferedReader buffReader;
		Token teste;

		try {
			fReader = new FileReader("ARQUIVOTESTE_WINDOWS.C");
			buffReader = new BufferedReader(fReader);

			while (true) {
				teste = Scanner.getInstancia().executar(buffReader);

				if (teste == null) {
					break;
				} else {
					System.out.println(teste.getClassificacao().getDescricao() + ", " + teste.getLexema());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExcecaoClassificacaoInexistente e) {
			e.printStackTrace();
		} catch (ExcecaoCompilador e) {
			e.printStackTrace();
		}
	}
}
