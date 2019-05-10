package Algoritmo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class ACO {

	private double[][] d;
	private double[][] feromonio;
	private double[][] dividendosProbabilidades;
	private double alfa, beta, Qk, ro;
	private ArrayList<Formiga> colonia;
	private ArrayList<Integer> _aVisitar;
	private Formiga melhorFormiga;
	private int numeroFormigas, numeroIteracoes;
	boolean[] cidadesSelecionadasK;
	private FileWriter arqFormigas, arqMelhorFormigas;
	private PrintWriter gravarArqFormigas, gravarArqMelhorFormigas;

	public ACO(double alfa, double beta, double qk, double ro, int numeroFormigas, int numeroIteracoes, String entrada,
			String saidaFormigas, String saidaMelhorFormiga) {
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
				feromonio[i][j] = 0.001;
			}
		}
		int iteracao = 0;
		String textoMelhorFormiga[] = new String[numeroIteracoes+1];
		String textoMelhorFormigaPopulacao[] = new String[numeroIteracoes+1];
		
		while (iteracao < numeroIteracoes) {
			System.out.println("\n IteraÃ§Ã£o: " + iteracao);
			//gravarArqFormigas.printf("\n IteraÃ§Ã£o: " + iteracao + "\n");
			colonia = new ArrayList<Formiga>();
			for (int k = 0; k < numeroFormigas; k++) {
				int[] caminhoFormigak = new int[d.length];
				for (int j = 0; j < caminhoFormigak.length; j++) {
					caminhoFormigak[j] = -1;
				}

				this._aVisitar = new ArrayList<Integer>();
				for (int i = 0; i < this.d.length; i++) {
					this._aVisitar.add(new Integer(i));
				}

				cidadesSelecionadasK = new boolean[d.length];

				Formiga formiga = new Formiga(caminhoFormigak);

				if (iteracao < (int)(this.numeroIteracoes/10)) {
					// Cria a rota aleatoria da formiga e atualiza a distancia
					this.criaRotaAleatoria(formiga);
				} else {
					// Cria a rota da formiga e atualiza a distancia
					this.criaRota(formiga);
				}

				colonia.add(formiga);
			}
			
			//Ranqueia a populaÃ§Ã£o 
			this.rank();

			// Atualizar Feromonio
			this.atualizaFeromomio();
			
			String mFormiga = "Melhor Formiga: ";
			mFormiga += melhorFormiga.getLk() + " : ";
			for (int j = 0; j < melhorFormiga.getSk().length; j++) {
				mFormiga += melhorFormiga.getSk()[j] + " ";
			}
			
			textoMelhorFormiga[iteracao] = mFormiga;
			
			String mFormigaPopulacao = "Melhor Formiga: ";
			mFormigaPopulacao += colonia.get(0).getLk() + " : ";
			for (int j = 0; j < colonia.get(0).getSk().length; j++) {
				mFormigaPopulacao += colonia.get(0).getSk()[j] + " ";
			}
			textoMelhorFormigaPopulacao[iteracao] = mFormigaPopulacao;
			
			colonia.clear();
			
			iteracao++;

		}
		
		
		for (String mFormiga : textoMelhorFormiga) {
			gravarArqMelhorFormigas.println(mFormiga);
		}
		
		for (String mFormigaPop : textoMelhorFormigaPopulacao) {
			gravarArqFormigas.println(mFormigaPop);
		}

		for (int i = 0; i < feromonio.length; i++) {
			for (int j = 0; j < feromonio.length; j++) {
				gravarArqFormigas.printf(feromonio[i][j] + "\t");
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
				this._aVisitar.remove(cidadeJ);
			} else {
				cidadeJ = this.selecionaCidadeJ(formiga, posicao);
				formiga.setCidade(posicao, cidadeJ);
				cidadesSelecionadasK[cidadeJ] = true;
				this._aVisitar.remove(new Integer(cidadeJ));
				// Calcular a distancia entre o elemento na posiÃ§Ã£o anterior e o
				// elemento inserido na posiÃ§Ã£o atual
				formiga.setLk(formiga.getLk() + d[formiga.getSk()[posicao - 1]][cidadeJ]);
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
			if (posicao > 0) {
				formiga.setLk(formiga.getLk() + d[formiga.getSk()[posicao - 1]][posicao]);
			}
		}
	}

	private void atualizaFeromomio() {

		double[][] delta = new double[feromonio.length][feromonio[0].length];

		for (Formiga formiga : colonia) {
			deltaFeromomio(formiga, delta);
		}

		// double p = Math.random();
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
	/**
	 * @param formiga
	 * @param posicao
	 * @return
	 */
	private int selecionaCidadeJ(Formiga formiga, int posicao) {
		int i = formiga.getSk()[posicao - 1];
		double aleatorio = Math.random();
		this.atualizaSomatorio(i);

		int escolhida = -1;

		// Seleciona somente as que nao foram escolhidas

		double somatorioProbabilidades = 0.0;
		ArrayList<Integer> aVisitar = (ArrayList<Integer>) this._aVisitar.clone();
		while (!aVisitar.isEmpty()) {
			int j = (int) aVisitar.remove(0);
			somatorioProbabilidades += getProbabilidade(i, j);
			if (aleatorio < somatorioProbabilidades) {
				escolhida = j;
				break;
			}

		}

		if (escolhida == -1) {
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

		
		ArrayList<Integer> aVisitar = (ArrayList<Integer>) this._aVisitar.clone();
		while (!aVisitar.isEmpty()) {
			int j = (int) aVisitar.remove(0);
			somatorio += dividendoProbCidade(i, j);
		}
		
	}

	// calcula os dividendos
	private double dividendoProbCidade(int i, int j) {
		dividendosProbabilidades[i][j] = Math.pow(feromonio[i][j], this.alfa) * Math.pow(1 / d[i][j], this.beta);
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

	
    //Ranqueamento da populaÃƒÂ§ÃƒÂ£o
	public void rank() {

		Collections.sort(this.colonia);
		
		if (this.melhorFormiga.getLk() > this.colonia.get(0).getLk()){
			this.melhorFormiga = new Formiga(this.colonia.get(0).getLk(), this.colonia.get(0).getSk());
		}
	}

	
	// Realiza aleitura do arquivo do tsp com as distÃƒÂ¢ncias ou coordenadas
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
		ACO aco = new ACO(1, 6, 0.1, 0.2, 200, 2000, entrada, saidaFormigas, melhorFormiga);
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
