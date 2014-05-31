/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
package principal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import util.Scanner;
import util.Token;
import excecoes.ExcecaoClassificacaoInexistente;
import excecoes.ExcecaoCompilador;

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
			fReader = new FileReader(args[0]);
			buffReader = new BufferedReader(fReader);

			while (true) {
				teste = Scanner.getInstancia().executar(buffReader);

				if (teste == null) {
					break;
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
