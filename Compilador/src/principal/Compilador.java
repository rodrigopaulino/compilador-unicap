package principal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import util.Scanner;
import util.Token;
import excecoes.ExcecaoClassificacaoInexistente;
import excecoes.ExcecaoCompilador;

public class Compilador {

	/** 
	 * @param args
	 */
	public static void main(String[] args) {
		FileReader fReader;
		BufferedReader buffReader;
		Token teste;
		
		try {
			fReader = new FileReader("teste.C");
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
