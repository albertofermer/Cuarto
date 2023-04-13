import numpy as np
import Utils

'''
Particle Swarm Optimization con vecindad global.
'''
def Global_PSO(semilla, funcion, vel, iteraciones_sin_mejora):
    np.random.seed(semilla) # Inicializamos la semilla
    idfun = 0 if funcion == Utils.RosenbrockFunction else 1     # Obtenemos el identificador de cada funcion de evaluación
    t = 0   # Numero de iteraciones a 0.
    # Inicializar X y V (Valores aleatorios entre MAX y MIN). Cada función tiene límites diferentes.
    if idfun == 0:  # Rosenbrock
        _x = (np.random.uniform(Utils.MIN_POS_ROS_X, Utils.MAX_POS_ROS_X, Utils.NUM_PARTICLES))
        y = (np.random.uniform(Utils.MIN_POS_ROS_Y, Utils.MAX_POS_ROS_Y, Utils.NUM_PARTICLES))
        x = np.column_stack((_x, y))
    else:   # Rastrigin
        x = np.random.uniform(Utils.MIN_POS_RAS, Utils.MAX_POS_RAS, (Utils.NUM_PARTICLES, Utils.DIMENSION))
    # La velocidad es aleatoria entre -VEL_MAX y +VEL_MAX
    v = np.random.uniform(vel[0], vel[1], (Utils.NUM_PARTICLES, Utils.DIMENSION))
    pbest = x.copy()  # Al principio, cada particula tiene como mejor posición ella misma.
    # Se inicializa el numero de evaluaciones a la longitud de pbest porque se aplica la funcion
    # de evaluación a cada particula para generar best_valores.
    num_evaluaciones = len(pbest)
    best_valores = np.apply_along_axis(funcion, 1, pbest)  # Aplica la funcion por filas a la matriz.
    gbest = [10, 10] # gbest arbitrario, puede ser cualquiera que no sea el óptimo.
    mejor_valor = float("inf")  # El mejor valor se inicializa al máximo
    mejor_posicion = np.ones(Utils.DIMENSION) # Da igual a qué se inicalice porque no se usa en las comparaciones.

    # Listas para generar las graficas
    mejores_posiciones = []
    mejores_valores = []
    coordenadas = x.copy()
    # Mientras no se cumpla la condición (numero de iteraciones sin mejorar la solución)
    while t < iteraciones_sin_mejora:
        for i in range(Utils.NUM_PARTICLES):
            num_evaluaciones += 1
            # Evalúo la partícula i
            valor = funcion(x[i, :])
            num_evaluaciones += 1
            # Obtengo el valor de la mejor posición de la partícula i
            pbest_valor = funcion(pbest[i, :])
            # Si es mejor, actualizamos la posición
            if valor < pbest_valor:
                pbest[i, :] = x[i, :].copy()
                best_valores[i] = valor
                num_evaluaciones += 1
                pbest_valor = funcion(pbest[i, :])

            num_evaluaciones += 1
            # La mejor posición global será la posición de la partícula que mejor fitness tenga.
            if pbest_valor < funcion(gbest):
                gbest = pbest[i, :].copy()

            # Actualizamos el mejor valor de la partícula
            if pbest_valor < mejor_valor:
                t = 0
                mejor_valor = pbest_valor
                mejor_posicion = x[i, :].copy()
                # print(mejor_valor)
                # print(mejor_posicion)

        # Para cada partícula:
        for i in range(Utils.NUM_PARTICLES):
            # Calcular v[i] de acuerdo a pbest[i] y gbest[i]
            # Calcular nueva posicion x[i]
            x[i], v[i] = Utils.GetNextPositionAndSpeed(Utils.OMEGA, v[i], Utils.PHI_1, pbest[i, :],
                                                       Utils.PHI_2, gbest, x[i, :], vel, idfun)
        # Actualizamos las coordenadas
        coordenadas = np.concatenate((coordenadas, x.copy()), axis=0)
        t += 1
        mejores_posiciones.append(mejor_posicion)
        mejores_valores.append(mejor_valor)
    return mejor_posicion, mejor_valor, mejores_posiciones, mejores_valores, num_evaluaciones, coordenadas
