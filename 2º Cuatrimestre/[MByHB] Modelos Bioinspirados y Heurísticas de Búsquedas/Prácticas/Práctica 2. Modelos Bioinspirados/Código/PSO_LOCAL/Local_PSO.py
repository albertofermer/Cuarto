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
    if idfun == 0:  # Si la funcion es Rosenbrock
        _x = (np.random.uniform(Utils.MIN_POS_ROS_X, Utils.MAX_POS_ROS_X, Utils.NUM_PARTICLES))
        y = (np.random.uniform(Utils.MIN_POS_ROS_Y, Utils.MAX_POS_ROS_Y, Utils.NUM_PARTICLES))
        x = np.column_stack((_x, y))
    else: # Si la funcion es Rastrigin
        x = np.random.uniform(Utils.MIN_POS_RAS, Utils.MAX_POS_RAS, (Utils.NUM_PARTICLES, Utils.DIMENSION))

    # La velocidad se inicializa en un número aleatorio entre -VEL_MAX y +VEL_MAX
    v = np.random.uniform(vel[0], vel[1], (Utils.NUM_PARTICLES, Utils.DIMENSION))

    # Al principio, cada particula tiene como mejor posición ella misma.
    pbest = x.copy()
    # Inicializamos el número de evaluaciones al tamaño de pbest porque se aplica la función
    # de evaluación a cada partícula al principio.
    num_evaluaciones = len(pbest)
    best_valores = np.apply_along_axis(funcion, 1, pbest)  # Aplica la funcion por filas a la matriz.
    # La mejor posición local y el valor
    # se incializan a infinito porque, al comienzo no hay ninguna mejor.
    lbest = np.ones((Utils.NUM_PARTICLES, Utils.DIMENSION)) * float('inf')
    mejor_valor = float("inf")
    mejor_posicion = np.ones(Utils.DIMENSION)
    # Listas para representar las gráficas.
    mejores_posiciones = []
    mejores_valores = []
    coordenadas = x.copy()
    # Mientas no se cumpla la condición de parada (numero de iteraciones sin mejora)
    while t < iteraciones_sin_mejora:
        # Para cada partícula de la nube:
        for i in range(Utils.NUM_PARTICLES):
            num_evaluaciones += 1
            # Calculamos el valor de la partícula
            valor = funcion(x[i, :])
            num_evaluaciones += 1
            # Calculamos el mejor valor de la partícula
            pbest_valor = funcion(pbest[i, :])
            # Si el valor de la partícula es mejor que el mejor valor, actualizamos pBest.
            if valor < pbest_valor:  # Encontrar el mínimo
                pbest[i, :] = x[i, :].copy()
                best_valores[i] = valor
                num_evaluaciones += 1
                pbest_valor = funcion(pbest[i, :])

            # Almacenamos el mejor valor hasta ahora
            if pbest_valor < mejor_valor:
                mejor_valor = pbest_valor
                mejor_posicion = x[i, :].copy()  # TODO: mejor_posicion -> matriz
                t = 0

        for i in range(Utils.NUM_PARTICLES):
            # Escoger lbest[i], la partícula con mejor fitness del entorno de x[i]
            num_evaluaciones += 2*Utils.VECINDAD # Aplicamos la funcion de evaluacion al entorno (4 particulas)
            # Obtenemos el índice de la partícula que minimice la función de evaluación
            lbest_index = np.argmin(np.apply_along_axis(funcion, 1, x[Utils.GetEntorno(i), :]))
            # Guardamos la posición de la partícula en lbest
            lbest[i, :] = x[lbest_index].copy()
            # Calcular v[i] de acuerdo a pbest[i] y lbest[i]
            # Calcular nueva posicion x[i]
            x[i], v[i] = Utils.GetNextPositionAndSpeed(Utils.OMEGA, v[i], Utils.PHI_1, pbest[i, :],
                                                       Utils.PHI_2, lbest[i, :], x[i, :], vel, idfun)
        # Aumenta el numero de iterciones
        t += 1
        # Añadimos las coordenadas de la nube de partículas a la matriz de coordenadas
        coordenadas = np.concatenate((coordenadas, x.copy()), axis=0)
        # Añadimos la mejor posición a la lista de mejores posiciones
        mejores_posiciones.append(mejor_posicion)
        # Lo mismo con los valores.
        mejores_valores.append(mejor_valor)
    return mejor_posicion, mejor_valor, mejores_posiciones, mejores_valores, num_evaluaciones, coordenadas