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
	private int numeroFormigas, numeroIteracoes, selecao;
	boolean[] cidadesSelecionadasK;
	private FileWriter arqPopulacao, arqMelhorGlobal;
	private PrintWriter gravarArqPopulacao, gravarArqMelhorGlobal;
	private Random random;

	public ACO(double alfa, double beta, double qk, double ro, int numeroFormigas, int numeroIteracoes, int selecao, String entrada,
			String saidaPopulacao, String saidaMelhorGlobal) {
		if (entrada.contains("brazil27")) {
			this.iniciarAmbienteBrazil27(entrada);
		} else
			this.iniciarAmbiente(entrada);
		this.alfa = alfa;
		this.beta = beta;
		this.Qk = qk;
		this.ro = ro;
		this.numeroFormigas = numeroFormigas;
		this.numeroIteracoes = numeroIteracoes;
		this.selecao = selecao;
		this.feromonio = new double[d.length][d.length];
		this.dividendosProbabilidades = new double[d.length][d.length];
		try {
			this.arqPopulacao = new FileWriter(saidaPopulacao);
			this.arqMelhorGlobal = new FileWriter(saidaMelhorGlobal);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.gravarArqPopulacao = new PrintWriter(arqPopulacao);
		this.gravarArqMelhorGlobal = new PrintWriter(arqMelhorGlobal);
		random = new Random(12345);
	}

	private void iniciar() {
		melhorFormiga = new Formiga();
		for (int i = 0; i < feromonio.length; i++) {
			for (int j = 0; j < feromonio.length; j++) {
				feromonio[i][j] = 0.001;
			}
		}
		int iteracao = 0;
		String textoMelhorGlobal[] = new String[numeroIteracoes];
		String textoMelhorFormigaPopulacao[] = new String[numeroIteracoes];
		String textoMediaPopulacao[] = new String[numeroIteracoes];
		String textoPiorFormigaPopulacao[] = new String[numeroIteracoes];

		while (iteracao < numeroIteracoes) {
			
			colonia = new ArrayList<Formiga>();
			for (int k = 0; k < numeroFormigas; k++) {
				int[] caminhoFormigak = new int[d.length + 1];
				for (int j = 0; j < caminhoFormigak.length; j++) {
					caminhoFormigak[j] = -1;
				}

				this._aVisitar = new ArrayList<Integer>();
				for (int i = 0; i < this.d.length; i++) {
					this._aVisitar.add(new Integer(i));
				}

				cidadesSelecionadasK = new boolean[d.length];

				Formiga formiga = new Formiga(caminhoFormigak);

				if (iteracao < (int) (this.numeroIteracoes / 10)) {
					// Cria a rota aleatoria da formiga e atualiza a distancia
					this.criaRotaAleatoria(formiga);
				} else {
					// Cria a rota da formiga e atualiza a distancia
					this.criaRota(formiga);
				}

				colonia.add(formiga);
			}

			// Ranqueia a populaÃ§Ã£o
			this.rank();

			// Atualizar Feromonio
			this.atualizaFeromomio();

			textoMelhorGlobal[iteracao] = iteracao+"\t"+melhorFormiga.getLk();

			// Media de fitness da colonia
			double fitnessMedio = 0.0;
			for (Formiga formiga : colonia) {
				fitnessMedio += formiga.getLk();
			}
			fitnessMedio = fitnessMedio / colonia.size();
			textoMediaPopulacao[iteracao] = String.format("%.2f", fitnessMedio);

			// Melhor Formiga colonia
			textoMelhorFormigaPopulacao[iteracao] = colonia.get(0).getLk() + "";

			// Pior Formiga colonia
			textoPiorFormigaPopulacao[iteracao] = String.format("%.2f", colonia.get(colonia.size() - 1).getLk());

			colonia.clear();

			iteracao++;

		}

		System.out.println(textoMelhorGlobal[textoMelhorGlobal.length - 1]);

		for (String mFormiga : textoMelhorGlobal) {
			gravarArqMelhorGlobal.println(mFormiga);
		}

		for (int i = 0; i < textoMelhorFormigaPopulacao.length; i++) {
			gravarArqPopulacao.println(i+"\t"+textoMelhorFormigaPopulacao[i] + "\t" + textoMediaPopulacao[i] + "\t"
					+ textoPiorFormigaPopulacao[i]);
		}
/*
		for (int i = 0; i < feromonio.length; i++) {
			for (int j = 0; j < feromonio.length; j++) {
				gravarArqPopulacao.printf(feromonio[i][j] + "\t");
			}
			gravarArqPopulacao.printf("\n");
		}*/

		try {
			gravarArqPopulacao.close();
			arqPopulacao.close();
			gravarArqMelhorGlobal.close();
			arqMelhorGlobal.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void criaRota(Formiga formiga) {
		for (int posicao = 0; posicao < formiga.getSk().length - 1; posicao++) {

			int cidadeJ = -1;

			if (posicao == 0) {
				cidadeJ = this._aVisitar.get(random.nextInt(this._aVisitar.size()));;
				formiga.setCidade(posicao, cidadeJ);
				cidadesSelecionadasK[cidadeJ] = true;
				this._aVisitar.remove(new Integer(cidadeJ));
			} else {
				if(this.selecao == 0){
					cidadeJ = this.selecionaCidadeJRoleta(formiga, posicao);
				}
				else if(this.selecao == 1){
					cidadeJ = this.selecionaCidadeJTorneio(formiga, posicao);
				}
				
				formiga.setCidade(posicao, cidadeJ);
				cidadesSelecionadasK[cidadeJ] = true;
				this._aVisitar.remove(new Integer(cidadeJ));
				// Calcular a distancia entre o elemento na posiÃ§Ã£o
				// anterior e o
				// elemento inserido na posiÃ§Ã£o atual
				formiga.setLk(formiga.getLk() + d[formiga.getSk()[posicao - 1]][cidadeJ]);
			}
		}

		formiga.setCidade(formiga.getSk().length - 1, formiga.getSk()[0]);
		int ultima = formiga.getSk()[formiga.getSk().length - 2];
		int primeira = formiga.getSk()[formiga.getSk().length - 1];
		double distancia_ultima_primeira = d[ultima][primeira];
		formiga.setLk(formiga.getLk() + distancia_ultima_primeira);
	}

	private void criaRotaAleatoria(Formiga formiga) {
		for (int posicao = 0; posicao < formiga.getSk().length - 1; posicao++) {

			int cidadeJ = -1;
			cidadeJ = this._aVisitar.get(random.nextInt(this._aVisitar.size()));
			formiga.setCidade(posicao, cidadeJ);
			cidadesSelecionadasK[cidadeJ] = true;
			this._aVisitar.remove(new Integer(cidadeJ));
			if (posicao > 0) {
				formiga.setLk(formiga.getLk() + d[formiga.getSk()[posicao - 1]][posicao]);
			}
		}

		formiga.setCidade(formiga.getSk().length - 1, formiga.getSk()[0]);
		int ultima = formiga.getSk()[formiga.getSk().length - 2];
		int primeira = formiga.getSk()[formiga.getSk().length - 1];
		double distancia_ultima_primeira = d[ultima][primeira];
		formiga.setLk(formiga.getLk() + distancia_ultima_primeira);
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
	private int selecionaCidadeJRoleta(Formiga formiga, int posicao) {
		int i = formiga.getSk()[posicao - 1];
		double aleatorio = random.nextDouble();
		this.atualizaSomatorio(i);

		int escolhida = -1;

		// Seleciona somente as que nao foram escolhidas

		double somatorioProbabilidades = 0.0;
		ArrayList<Integer> aVisitar = (ArrayList<Integer>) this._aVisitar.clone();
		while (!aVisitar.isEmpty()) {
			int j = (int) aVisitar.remove(0);
			double prob = getProbabilidade(i, j);
			somatorioProbabilidades += prob;
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

	private int selecionaCidadeJTorneio(Formiga formiga, int posicao) {
		int i = formiga.getSk()[posicao - 1];
		// double aleatorio = random.nextDouble();
		// this.atualizaSomatorio(i);

		int escolhida = -1;

		// Seleciona somente as que nao foram escolhidas

		double somatorioProbabilidades = 0.0;
		ArrayList<Integer> aVisitar = (ArrayList<Integer>) this._aVisitar.clone();
		ArrayList<Integer> aEscolherAleatorio = new ArrayList<Integer>();
		int tamanhoAVisitar = aVisitar.size();
		int tamanhoTorneio = (aVisitar.size() * 0.1) < 4 ? 4 : (int) (aVisitar.size() * 0.1);
		// System.out.println(tamanhoAVisitar);
		while (aEscolherAleatorio.size() < tamanhoTorneio && aEscolherAleatorio.size() < tamanhoAVisitar) {
			int j = (int) aVisitar.remove(random.nextInt(aVisitar.size()));
			aEscolherAleatorio.add(j);
		}

		// double feromonioC = this.getFeromonio(i, aEscolherAleatorio.get(0));
		double dividendo = this.dividendoProbCidade(i, aEscolherAleatorio.get(0));

		escolhida = aEscolherAleatorio.get(0);

		for (int j = 0; j < aEscolherAleatorio.size(); j++) {
			// if(feromonioC > this.getFeromonio(i, aEscolherAleatorio.get(j)))
			// {
			if (dividendo > this.dividendoProbCidade(i, aEscolherAleatorio.get(j))) {
				// feromonioC = this.getFeromonio(i, aEscolherAleatorio.get(j));
				dividendo = this.dividendoProbCidade(i, aEscolherAleatorio.get(j));
				escolhida = aEscolherAleatorio.get(j);
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

	// Ranqueamento da populaÃƒÂ§ÃƒÂ£o
	public void rank() {

		Collections.sort(this.colonia);

		if (this.melhorFormiga.getLk() > this.colonia.get(0).getLk()) {
			this.melhorFormiga = new Formiga(this.colonia.get(0).getLk(), this.colonia.get(0).getSk());
		}
	}

	// Realiza aleitura do arquivo do tsp com as distÃƒÂ¢ncias ou
	// coordenadas
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

	// Realiza aleitura do arquivo do tsp com as distÃƒÂ¢ncias
	private void iniciarAmbienteBrazil27(String path) {
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
				for (int z = 0; z < linha.length; z++) {
					if (linha[z].contentEquals("Inf")) {
						E[i][z] = 1000000000;
					} else
						E[i][z] = Double.parseDouble(linha[z]);

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

		double[] alfa = { 1, 0.5, 0.01};
		double[] beta = { 6, 3, 1};
		double[] q = { 0.5, 1, 0.01};
		double[] ro = { 0.2, 0.1, 0.05};
		int[] tamColonia = { 58 };
		int[] iteracoes = { 2000 };
		//0 - roleta, 1 - torneio
		int[] selecao = {0, 1};
		String[] problema = { "brazil27", "brazil58" };			
		for (int pr = 0; pr < problema.length; pr++) {
			for (int se = 0; se < selecao.length; se++) {	
			for (int i = 0; i < alfa.length; i++) {
				for (int j = 0; j < beta.length; j++) {
					for (int j2 = 0; j2 < q.length; j2++) {
						for (int k = 0; k < ro.length; k++) {
							for (int k2 = 0; k2 < tamColonia.length; k2++) {
								for (int l = 0; l < iteracoes.length; l++) {
									String entrada = "..\\ACO_NOVO\\src\\Testes\\" + problema[pr] + ".tsp";
									String saidaPopulacao = "..\\ACO_NOVO\\src\\Testes\\Testes_execucoes_" + problema[pr]
											+ "_saidaPopulacao tamColonia-" + tamColonia[k2] + "_iteracoes-"
											+ iteracoes[l] + "_selecao-"+ selecao[se] + "_alfa-" + alfa[i] + "_beta-" + beta[j] + "_feromonio-"
											+ q[j2] + "_ro-" + ro[k] + ".txt";
									String saidaMelhorGlobal = "..\\ACO_NOVO\\src\\Testes\\Testes_execucoes_" + problema[pr]
											+ "_saidaMelhorGlobal tamColonia-" + tamColonia[k2] + "_iteracoes-"
											+ iteracoes[l] + "_selecao-"+ selecao[se] + "_alfa-" + alfa[i] + "_beta-" + beta[j] + "_feromonio-"
											+ q[j2] + "_ro-" + ro[k] + ".txt";

									ACO aco = new ACO(alfa[i], beta[j], q[j2], ro[k], tamColonia[k2],
											iteracoes[l], selecao[se], entrada, saidaPopulacao, saidaMelhorGlobal);
									
									System.out.print("\n\nTeste\t" + problema[pr]
											+ "\ttamColonia\t" + tamColonia[k2] + "\titeracoes\t"
											+ iteracoes[l] + "\tselecao\t"+ selecao[se] + "\talfa\t" + alfa[i] + "\tbeta\t" + beta[j] + "\tQ\t"
											+ q[j2] + "\tro\t" + ro[k]+"\t");
									
									aco.iniciar();
								}
							}
						}
					}
					}
				}
			}
		}
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
		if (this.Lk < outraFormiga.getLk()) {
			return -1;
		}
		if (this.Lk > outraFormiga.getLk()) {
			return 1;
		}
		return 0;
	}

}