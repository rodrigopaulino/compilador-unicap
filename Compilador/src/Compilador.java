/*
 * Este arquivo é propriedade de Rodrigo Paulino Ferreira de Souza.
 * Nenhuma informação nele contida pode ser reproduzida,
 * mostrada ou revelada sem permissão escrita do mesmo.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

		try {
			fReader = new FileReader(args[0]);
			buffReader = new BufferedReader(fReader);

			Parser.getInstancia().executar(buffReader);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExcecaoCompilador e) {
			System.out.print(e.getMessage());
		}
	}
}
