import numpy as np
import Utils
import matplotlib.pyplot as plt

'''
Particle Swarm Optimization con vecindad local.
'''
def Local_PSO(semilla, funcion, vel, iteraciones_sin_mejora):
    np.random.seed(semilla) # Inicializa la semilla
    idfun = 0 if funcion == Utils.RosenbrockFunction else 1 # Detecta qué función de evaluación tiene que utilizar
    t = 0   # Inicializamos el contador de iteraciones a 0
    #  Inicializamos X y V (Valores aleatorios entre MAX y MIN)
    # Dependiendo de la función de evaluación, las gráficas tendrán unos límites
    # u otros.
    if idfun == 0:  # Si la funci
        _x = (np.random.uniform(Utils.MIN_POS_ROS_X, Utils.MAX_POS_ROS_X, Utils.NUM_PARTICLES))
        y = (np.random.uniform(Utils.MIN_POS_ROS_Y, Utils.MAX_POS_ROS_Y, Utils.NUM_PARTICLES))
        x = np.column_stack((_x, y))
    else: # Si la funcion es Rastrigin
        x = np.random.uniform(Utils.MIN_POS_RAS, Utils.MAX_POS_RAS, (Utils.NUM_PARTICLES, Utils.DIMENSION))

    v = np.random.uniform(vel[0], vel[1], (Utils.NUM_PARTICLES, Utils.DIMENSION))

    pbest = x.copy()  # Al principio, cada particula tiene como mejor posición ella misma.
    num_evaluaciones = len(pbest)
    best_valores = np.apply_along_axis(funcion, 1, pbest)  # Aplica la funcion por filas a la matriz.
    lbest = np.ones((Utils.NUM_PARTICLES, Utils.DIMENSION)) * float('inf')

    mejor_valor = float("inf")
    mejor_posicion = np.ones(Utils.DIMENSION)
    # Graficas
    mejores_posiciones = []
    mejores_valores = []
    coordenadas = x.copy()
    # Inicializar el entorno de cada partícula (comunicacion social)

    while t < iteraciones_sin_mejora:
        for i in range(Utils.NUM_PARTICLES):
            num_evaluaciones += 1
            valor = funcion(x[i, :])
            num_evaluaciones += 1
            pbest_valor = funcion(pbest[i, :])
            if valor < pbest_valor:  # Encontrar el mínimo
                pbest[i, :] = x[i, :].copy()
                best_valores[i] = valor
                num_evaluaciones += 1
                pbest_valor = funcion(pbest[i, :])

            if pbest_valor < mejor_valor:
                mejor_valor = pbest_valor
                mejor_posicion = x[i, :].copy()  # TODO: mejor_posicion -> matriz
                t = 0

        for i in range(Utils.NUM_PARTICLES):
            # Escoger lbest[i], la partícula con mejor fitness del entorno de x[i]
            num_evaluaciones += 2*Utils.VECINDAD
            lbest_index = np.argmin(np.apply_along_axis(funcion, 1, x[Utils.GetEntorno(i), :]))  # Consigo el indice
            lbest[i, :] = x[lbest_index].copy()  # Lo guardo en lbest
            # Calcular v[i] de acuerdo a pbest[i] y lbest[i]
            # Calcular nueva posicion x[i]
            x[i], v[i] = Utils.GetNextPositionAndSpeed(Utils.OMEGA, v[i], Utils.PHI_1, pbest[i, :],
                                                       Utils.PHI_2, lbest[i, :], x[i, :], vel, idfun)
        t += 1
        coordenadas = np.concatenate((coordenadas, x.copy()), axis=0)
        mejores_posiciones.append(mejor_posicion)
        mejores_valores.append(mejor_valor)
    return mejor_posicion, mejor_valor, mejores_posiciones, mejores_valores, num_evaluaciones, coordenadas


if __name__ == "__main__":
    # Utils.generarVideos(Local_PSO)
    Utils.graficas_resultados(Local_PSO)
    #
    # np.set_printoptions(suppress=True) # Para quitar la notación científica.
    # mejor_ros, mejor_ras = Utils.experimentacion(Local_PSO)
    # print(f"ROSENBROCK:\n {mejor_ros}")
    # print(f"RASTRIGIN:\n {mejor_ras}")
