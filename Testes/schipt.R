library(stringr)
alfa <- c(1)
beta <- c(6)
q <- c(0.5,1,0.01)
ro <- c(0.2,0.1,0.05)
tamColonia <- c(58)
iteracoes <- c(2000)
selecao <- c(0)
problema <- c("brazil27", "brazil58")
pobSelecaoAleatoria <- c(0,0.01)

for(i in alfa){
	for(j in beta){
		for(k in q){
			for(l in ro){
				for(m in tamColonia){
					for(n in iteracoes){
						for(o in selecao){
							for(p in problema){
								for(ps in pobSelecaoAleatoria){
									t <- str_c(i," ",j, 
 " ", k, " ", l,  " " ,m, " "  ,n, " "  ,o," "  , p,  " "   , ps)
									print(t)
								}		
							}
						}		
					}
				}
			}
		
		}
		
	}
}
