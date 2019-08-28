import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class LerArquivo {

	private double[][] iniciarAmbiente(String path) {
		try {

			double[][] E;

			int dimensao = 0;
			Scanner f = new Scanner(new File(path));
			String s = f.nextLine();

			while (!s.contains("DIMENSION: ")) {
				s = f.nextLine();
			}
			dimensao = Integer.parseInt(s.split(" ")[1]);

			E = new double[dimensao][dimensao];

			while (!s.contains("EDGE_WEIGHT_SECTION")) {
				s = f.nextLine();
			}

			int i = 0;
			s = f.nextLine();
			while (f.hasNext() && (!s.equals("EOF") || !s.equals("eof"))) {

				String[] linha = s.split(" ");
				int j = i + 1;
				for (int z = 0; z < linha.length; z++) {
					E[i][j] = Double.parseDouble(linha[z]);
					E[j][i] = Double.parseDouble(linha[z]);
					j++;
				}
				i++;
				s = f.nextLine();
			}

			f.close();

			return E;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	public static void main(String[] args) {
		LerArquivo la = new LerArquivo();
		double E[][] = la.iniciarAmbiente("..\\ACO\\src\\Testes\\brazil58.tsp");
		
		FileWriter arqSaidas;
		try {
			arqSaidas = new FileWriter("..\\ACO\\src\\Testes\\brazil.txt");
			PrintWriter saida = new PrintWriter(arqSaidas);
			
			for (int i = 0; i < E.length; i++) {
				for (int j = 0; j < E[i].length; j++) {
					saida.print(E[i][j]+"\t");
				}
				saida.print("\n");
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
