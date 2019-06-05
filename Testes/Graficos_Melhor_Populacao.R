library(stringr)
alfa <- c("1.0","0.5","0.01")
beta <- c("6.0","3.0","1.0")
q <- c("0.5","1.0","0.01")
ro <- c("0.2","0.1","0.05")
tamColonia <- c("58")
iteracoes <- c("2000")
selecao <- c("0","1")
problema <- c("brazil27", "brazil58")
pobSelecaoAleatoria <- c("0.0","0.01")

for(i in alfa){
	for(j in beta){
		for(k in q){
			for(l in ro){
				for(m in tamColonia){
					for(n in iteracoes){
						for(o in selecao){
							for(p in problema){
								Origem1 <- str_c("C:\\Workspace\\ACO_NOVO\\src\\Testes\\Testes_execucoes_",p)
								Origem2 <- str_c("_saidaPopulacao tamColonia-")
								Origem3 <- str_c(m,"_iteracoes-",n,"_selecao-",o,"_alfa-",i,"_beta-",j,"_feromonio-",k,"_ro-",l)
								Dados.brutos <- read.table(str_c(Origem1,Origem2,Origem3,".txt"), header = FALSE)
								Dados1 <- data.frame(Iteracoes = Dados.brutos$V1, Distancia_Melhor = Dados.brutos$V2)
								jpeg(filename = str_c(Origem1,str_c("_saidaMelhorPopulacao tamColonia-"),Origem3,".jpg"),width = 960, height = 700,quality = 100, restoreConsole = TRUE)
								plot(Dados1, type = 'l', col='red')
								dev.off()										
							}
						}		
					}
				}
			}
		
		}
		
	}
}