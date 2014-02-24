package principal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import excecoes.ExcecaoClassificacaoInexistente;
import excecoes.ExcecaoCompilador;

import util.Scanner;

public class Compilador {

	/** 
	 * @param args
	 */
	public static void main(String[] args) {
		FileReader fReader;
		BufferedReader buffReader;
		
		try {
			fReader = new FileReader("teste.C");
			buffReader = new BufferedReader(fReader);
			
			while (true) {
				if (Scanner.getInstancia().executar(buffReader) == null) {
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
