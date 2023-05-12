import Utils
import numpy as np
import time
def SistemaHormigas(semilla, problema):
    # Lectura de las coordenadas del problema:
    dimension, ciudades = Utils.leerFicheroTSP(f"..\\FicherosTSP\\{problema}.tsp")
    dimension = int(dimension)
    # Inicializar matrices
    feromonas = np.ones((Utils.numeroHormigas,dimension))
    heuristica = Utils.inicializarMatrizDistancias(ciudades)
    visitados = np.zeros((dimension,dimension))
    solucionHormigas = np.ones((Utils.numeroHormigas,dimension), dtype=int) * 1
    coste = np.ones(Utils.numeroHormigas) * float('inf')
    MejorHormiga = solucionHormigas[0]
    # print(solucionHormigas)
    start = time.time()
    while (time.time() - start) < 10:
        # Para k=1 hasta NumeroHormigas hacer:
        for k in range(Utils.numeroHormigas):
            solucionHormigas[k][0] = 0  # Nodo Inicial
            coste[k] = Utils.funcionCoste(heuristica, solucionHormigas[k]) # Calcula el Coste
            if Utils.funcionCoste(heuristica, MejorHormiga) < Utils.funcionCoste(heuristica, solucionHormigas[k]):
                MejorHormiga = solucionHormigas[k].copy()



    print(solucionHormigas)
if __name__ == "__main__":
    SistemaHormigas(123456, "ch130")