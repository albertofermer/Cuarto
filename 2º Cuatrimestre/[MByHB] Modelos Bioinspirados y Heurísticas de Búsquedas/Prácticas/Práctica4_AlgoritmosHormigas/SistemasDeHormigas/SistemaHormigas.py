import Utils
import numpy as np

def SistemaHormigas(semilla, problema):
    # Lectura de las coordenadas del problema:
    dimension, ciudades = Utils.leerFicheroTSP(f"..\\FicherosTSP\\{problema}.tsp")
    dimension = int(dimension)
    # Inicializar matrices
    feromonas = np.ones((Utils.numeroHormigas,dimension))
    heuristica = Utils.inicializarMatrizDistancias(ciudades)



if __name__ == "__main__":
    SistemaHormigas(123456, "ch130")