package Algoritmo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ACO {

	private double[][] d;
	private double[][] feromonio;
	private double[][] dividendosProbabilidades;
	private double alfa, beta, Qk, ro;
	private ArrayList<Formiga> colonia;
	private Formiga melhorFormiga;
	private int numeroFormigas, numeroIteracoes;
	boolean[] cidadesSelecionadasK;
	private FileWriter arqFormigas, arqMelhorFormigas;
	private PrintWriter gravarArqFormigas, gravarArqMelhorFormigas;

	public ACO(double alfa, double beta, double qk, double ro, int numeroFormigas, int numeroIteracoes, String entrada, String saidaFormigas, String saidaMelhorFormiga) {
		this.iniciarAmbiente(entrada);
		this.alfa = alfa;
		this.beta = beta;
		this.Qk = qk;
		this.ro = ro;
		this.numeroFormigas = numeroFormigas;
		this.numeroIteracoes = numeroIteracoes;
		this.feromonio = new double[d.length][d.length];
		this.dividendosProbabilidades = new double[d.length][d.length];
		try {
			this.arqFormigas = new FileWriter(saidaFormigas);
			this.arqMelhorFormigas = new FileWriter(saidaMelhorFormiga);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.gravarArqFormigas = new PrintWriter(arqFormigas);
		this.gravarArqMelhorFormigas = new PrintWriter(arqMelhorFormigas);
	}

	private void iniciar() {
		melhorFormiga = new Formiga();
		for (int i = 0; i < feromonio.length; i++) {
			for (int j = 0; j < feromonio.length; j++) {
				feromonio[i][j] = 0.1;
			}
		}
		int iteracao = 0;
		while (iteracao++ < numeroIteracoes) {
			System.out.println("\n Iteração: " + iteracao);
			gravarArqFormigas.printf("\n Iteração: " + iteracao + "\n");
			colonia = new ArrayList<Formiga>();
			for (int k = 0; k < numeroFormigas; k++) {
				int[] caminhoFormigak = new int[d.length];
				for (int j = 0; j < caminhoFormigak.length; j++) {
					caminhoFormigak[j] = -1;
				}
				
				cidadesSelecionadasK = new boolean[d.length]; 
				
				Formiga formiga = new Formiga(caminhoFormigak);

				if(iteracao == 0) {
					// Cria a rota aleatoria da formiga e atualiza a distancia
					this.criaRotaAleatoria(formiga);
				}
				else {
					// Cria a rota da formiga e atualiza a distancia
					this.criaRota(formiga);
				}
				
				//System.out.println(formiga.getLk());
				
				if (formiga.getLk() < melhorFormiga.getLk()) {
					melhorFormiga.setSk(formiga.getSk());
					melhorFormiga.setLk(formiga.getLk());					
				}
				
				gravarArqFormigas.printf("Formiga: " + k + " : ");
				
				for (int j = 0; j < formiga.getSk().length; j++) {
					gravarArqFormigas.printf(formiga.getSk()[j]+" ");
				}
				gravarArqFormigas.printf("\n");
				
				colonia.add(formiga);
			}

			// Atualizar Feromonio
			this.atualizaFeromomio();
			
			colonia.clear();
			
			gravarArqMelhorFormigas.printf("Melhor Formiga: ");
			
			gravarArqMelhorFormigas.printf(melhorFormiga.getLk()+" : ");
			
			for (int j = 0; j < melhorFormiga.getSk().length; j++) {
				gravarArqMelhorFormigas.printf(melhorFormiga.getSk()[j]+" ");
			}
			gravarArqMelhorFormigas.printf("\n");
			
		}
		
		for (int i = 0; i < feromonio.length; i++) {
			for (int j = 0; j < feromonio.length; j++) {
				gravarArqFormigas.printf(feromonio[i][j]+"\t");
			}
			gravarArqFormigas.printf("\n");
		}
				
		try {
			gravarArqFormigas.close();
			arqFormigas.close();
			gravarArqMelhorFormigas.close();
			arqMelhorFormigas.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void criaRota(Formiga formiga) {
		for (int posicao = 0; posicao < formiga.getSk().length; posicao++) {

			int cidadeJ = -1;

			if (posicao == 0) {
				Random r = new Random();
				cidadeJ = r.nextInt(formiga.getSk().length);
				formiga.setCidade(posicao, cidadeJ);
				cidadesSelecionadasK[cidadeJ] = true;
			} else {
				cidadeJ = this.selecionaCidadeJ(formiga, posicao);
				formiga.setCidade(posicao, cidadeJ);
				cidadesSelecionadasK[cidadeJ] = true;
				// Calcular a distancia entre o elemento na posição anterior e o
				// elemento inserido na posição atual
				formiga.setLk(formiga.getLk() + d[formiga.getSk()[posicao - 1]][posicao]);
			}
		}
	}
	
	private void criaRotaAleatoria(Formiga formiga) {
		for (int posicao = 0; posicao < formiga.getSk().length; posicao++) {

			int cidadeJ = -1;
			Random r = new Random();
			cidadeJ = r.nextInt(formiga.getSk().length);
			formiga.setCidade(posicao, cidadeJ);
			cidadesSelecionadasK[cidadeJ] = true;
			if(posicao > 0) {
				formiga.setLk(formiga.getLk() + d[formiga.getSk()[posicao - 1]][posicao]);
			}
		}
	}

	private void atualizaFeromomio() {

		double[][] delta = new double[feromonio.length][feromonio[0].length];

		for (Formiga formiga : colonia) {
			deltaFeromomio(formiga, delta);
		}

		//double p = Math.random();
		double p = this.ro;

		for (int i = 0; i < feromonio.length; i++) {
			for (int j = 0; j < feromonio[i].length; j++) {
				feromonio[i][j] = ((1 - p) * feromonio[i][j]) + delta[i][j];
			}
		}
	}

	// Calcula o delta de feromonio
	private void deltaFeromomio(Formiga formiga, double[][] delta) {

		double deltatIJk = (formiga.getSk().length * Qk) / formiga.getLk();

		for (int i = 0; i < formiga.getSk().length; i++) {

			int cidadeI = -1;
			int cidadeJ = -1;

			if (i == formiga.getSk().length - 1) {
				cidadeI = formiga.getSk()[i];
				cidadeJ = formiga.getSk()[0];
			} else {
				cidadeI = formiga.getSk()[i];
				cidadeJ = formiga.getSk()[i + 1];
			}

			delta[cidadeI][cidadeJ] = deltatIJk;
		}
	}

	// Roleta da escolha cidade
	private int selecionaCidadeJ(Formiga formiga, int posicao) {
		int i = formiga.getSk()[posicao - 1];
		double aleatorio = Math.random();
		this.atualizaSomatorio(i);

		int escolhida = -1;

		//Seleciona somente as que nao foram escolhidas
		double somatorioProbabilidades = 0.0;
		for (int j = 0; j < d[i].length; j++) {
			if(cidadesSelecionadasK[j] == false) {
				somatorioProbabilidades += getProbabilidade(i, j);
				if (aleatorio < somatorioProbabilidades) {
					escolhida = j;
					break;
				}
			}			
		}

		if(escolhida == -1) {
			System.out.println(escolhida);
		}
		
		return escolhida;
	}

	// probabilidade de estar na cidade i e ir para j
	private double getProbabilidade(int i, int j) {
		return this.dividendosProbabilidades[i][j] / somatorio;
	}

	// somatorio dos dividendos
	private double somatorio;

	private void atualizaSomatorio(int i) {
		somatorio = 0;
		for (int j = 0; j < d.length; j++) {
			if(cidadesSelecionadasK[j] == false) {
				somatorio += dividendoProbCidade(i, j);
			}
		}
	}

	// calcula os dividendos
	private double dividendoProbCidade(int i, int j) {
		dividendosProbabilidades[i][j] = Math.pow(feromonio[i][j], this.alfa) * Math.pow(1/d[i][j], this.beta);
		return dividendosProbabilidades[i][j];
	}

	// atualiza feromonio
	private void setaFeromonio(int i, int j, double novoFeromonio) {
		this.feromonio[i][j] = novoFeromonio;
	}

	// getdistancia
	private double getDistancia(int i, int j) {
		return this.d[i][j];
	}

	// getferomonio
	private double getFeromonio(int i, int j) {
		return this.feromonio[i][j];
	}

	// Realiza aleitura do arquivo do tsp com as distÃ¢ncias ou coordenadas
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

			this.d = E;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String entrada = "..\\ACO\\src\\Testes\\brazil58.tsp";
		String saidaFormigas = "..\\ACO\\src\\Testes\\saidaFormigas.txt";
		String melhorFormiga = "..\\ACO\\src\\Testes\\saidaMelhorFormiga.txt";
		ACO aco = new ACO(1,6,0.001,0.2,200,2000,entrada, saidaFormigas, melhorFormiga);
		aco.iniciar();
	}

}

class Formiga implements Comparable<Formiga> {

	private double Lk = Integer.MAX_VALUE;
	private int[] Sk;

	public Formiga() {

	}

	public Formiga(int[] Sk) {
		this.Lk = 0.0;
		this.Sk = Sk;
	}

	public Formiga(double Lk, int[] Sk) {
		super();
		this.Lk = Lk;
		this.Sk = Sk;
	}

	public double getLk() {
		return Lk;
	}

	public void setLk(double Lk) {
		this.Lk = Lk;
	}

	public int[] getSk() {
		return Sk;
	}

	public void setCidade(int posicao, int cidade) {
		this.Sk[posicao] = cidade;
	}

	public void setSk(int[] Sk) {
		this.Sk = Sk;
	}

	@Override
	public int compareTo(Formiga outraFormiga) {
		// TODO Auto-generated method stub
		if (this.Lk > outraFormiga.getLk()) {
			return -1;
		}
		if (this.Lk < outraFormiga.getLk()) {
			return 1;
		}
		return 0;
	}

}
