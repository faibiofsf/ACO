package ACOTSP;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ACO {

	private double[][] ambiente;
	private double[][] feromonio;
	private double[][] dividendosProbabilidades = new double[1][1];
	private double alfa, beta;
	
	
	//Roleta da escolha cidade
	private int selecionaCidadeJ(int i){
		return 0;
	}
	
	//probabilidade de estar na cidade i e ir para j
	private double getProbabilidade(int i,  int j){
		return this.dividendosProbabilidades[i][j] / somatorio;
	}
	
	//somatorio dos dividendos
	private double somatorio = 0;
	private void atualizaSomatorio(int i){
		for (int j = 0; j < ambiente.length; j++) {			
			somatorio += dividendoProbCidade(i, j);
		}
	}
	
	//calcula os dividendos
	private double dividendoProbCidade(int i, int j){
		dividendosProbabilidades[i][j] = Math.pow(ambiente[i][j], alfa) * Math.pow(feromonio[i][j], beta);
		return dividendosProbabilidades[i][j];
	}
	
	//atualiza feromonio
	private void atualizaFeromonio(int i,  int j, double novoFeromonio){
		this.feromonio[i][j] = novoFeromonio;
	}
	
	//getdistancia
	private double getDistancia(int i,  int j){
		return this.ambiente[i][j];
	}
		
	//getferomonio
	private double getFeromonio(int i,  int j){
		return this.feromonio[i][j];
	}
		
	//Realiza aleitura do arquivo do tsp com as distâncias ou coordenadas 
	private void iniciarAmbiente(String path) {
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

			this.ambiente = E;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}


class Formiga implements Comparable<Formiga> {

	private int fitness = 0;
	private String cromossomo;

	public Formiga() {
	}

	public Formiga(String cromossomo) {
		this.cromossomo = cromossomo;
	}

	public Formiga(int fitness, String cromossomo) {
		super();
		this.fitness = fitness;
		this.cromossomo = cromossomo;
	}

	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public String getCromossomo() {
		return cromossomo;
	}

	public void setCromossomo(String cromossomo) {
		this.cromossomo = cromossomo;
	}

	@Override
	public int compareTo(Formiga outraFormiga) {
		// TODO Auto-generated method stub
		if (this.fitness > outraFormiga.getFitness()) {
			return -1;
		}
		if (this.fitness < outraFormiga.getFitness()) {
			return 1;
		}
		return 0;
	}

}
