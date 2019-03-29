import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ACO {

	private double[][] d;
	private double[][] feromonio;
	private double[][] dividendosProbabilidades = new double[1][1];
	private double alfa, beta, Qk;
	private ArrayList<Formiga> colonia;
	private int numeroFormigas, numeroIteracoes;
		
	public ACO(double alfa, double beta, double qk, int numeroFormigas, int numeroIteracoes) {
		//teste teste
		this.alfa = alfa;
		this.beta = beta;
		this.Qk = qk;
		this.numeroFormigas = numeroFormigas;
		this.numeroIteracoes = numeroIteracoes;
	}

	private void iniciar() {
		
		
		
		while(numeroIteracoes-- > 0) {
			for (int i = 0; i < numeroFormigas; i++) {
				Formiga f = new Formiga(new int[d.length]);
				
				for (int posicao = 0; posicao < f.getSk().length; posicao++) {
					
					int cidadeJ = -1;
					
					if(posicao == 0 ) {
						Random r = new Random();						
						cidadeJ = r.nextInt(f.getSk().length);
					}
					else {
						cidadeJ = this.selecionaCidadeJ(f.getSk()[posicao-1]);
					}
					
					f.setCidade(posicao, cidadeJ);
					
				}
				
				//Calcular a distancia
				
				colonia.add(f);
			}
			
			//Atualizar Feromonio
			
		}
	}
	
	private void atualizaFeromomio() {
		
		double[][] delta = new double[feromonio.length][feromonio[0].length];
		
		for (Formiga formiga : colonia) {
			deltaFeromomio(formiga, delta);
		}
		
		double p = Math.random();
		
		for (int i = 0; i < feromonio.length; i++) {			
			for (int j = 0; j < feromonio[i].length; j++) {
				feromonio[i][j] = ((1-p)*feromonio[i][j]) + delta[i][j]; 
			}			
		}
	}
	
	//Calcula o delta de feromonio
	private void deltaFeromomio(Formiga formiga, double[][] delta) {
		
		double deltatIJk =  (formiga.getSk().length * Qk) / formiga.getLk();
		
		for (int i = 0; i < formiga.getSk().length; i++) {
			
			int cidadeI = -1;
			int cidadeJ = -1;
			
			if (i == formiga.getSk().length - 1) {
				cidadeI = formiga.getSk()[i];
				cidadeJ = formiga.getSk()[0];
			}
			else {
				cidadeI = formiga.getSk()[i];
				cidadeJ = formiga.getSk()[i+1];				
			}
			
			delta[cidadeI][cidadeJ] = deltatIJk;			
		}
	}
	
	//Roleta da escolha cidade
	private int selecionaCidadeJ(int i){
	    double aleatorio = Math.random();
	    this.somatorio = 0;
	    this.atualizaSomatorio(i);	    
	    
	    int escolhida = -1;
	    
	    for (int j = 0; j < d[i].length; j++) {
	    	if(aleatorio < getProbabilidade(i, j)) {
	    		escolhida = j;
	    		break;
	    	}
		}
	    
		return escolhida;
	}
	
	//probabilidade de estar na cidade i e ir para j
	private double getProbabilidade(int i,  int j){
		return this.dividendosProbabilidades[i][j] / somatorio;
	}
	
	//somatorio dos dividendos
	private double somatorio = 0;
	private void atualizaSomatorio(int i){
		for (int j = 0; j < d.length; j++) {			
			somatorio += dividendoProbCidade(i, j);
		}
	}
	
	//calcula os dividendos
	private double dividendoProbCidade(int i, int j){
		dividendosProbabilidades[i][j] = Math.pow(d[i][j], alfa) * Math.pow(feromonio[i][j], beta);
		return dividendosProbabilidades[i][j];
	}
	
	//atualiza feromonio
	private void setaFeromonio(int i,  int j, double novoFeromonio){
		this.feromonio[i][j] = novoFeromonio;
	}
	
	//getdistancia
	private double getDistancia(int i,  int j){
		return this.d[i][j];
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

			this.d = E;

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

	private double Lk = 0;
	private int[] Sk;

	public Formiga() {
		
	}

	public Formiga(int[] Sk) {
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
