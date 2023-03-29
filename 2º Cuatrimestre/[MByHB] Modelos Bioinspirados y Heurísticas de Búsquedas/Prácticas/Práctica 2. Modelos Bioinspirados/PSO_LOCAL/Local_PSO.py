import numpy as np
import Utils


def Local_PSO(semilla, dimensiones, funcion):
    np.random.seed(semilla)

    t = 0
    # Inicializar X y V (Valores aleatorios entre MAX y MIN)
    x = (np.random.random(size=(Utils.NUM_PARTICLES, dimensiones))) * (Utils.MAX_POS - Utils.MIN_POS) + Utils.MIN_POS
    v = (np.random.random(size=(Utils.NUM_PARTICLES, dimensiones))) * (Utils.MAX_VEL - Utils.MIN_VEL) + Utils.MIN_VEL
    pbest = x.copy()  # Al principio, cada particula tiene como mejor posición ella misma.
    best_valores = np.apply_along_axis(funcion, 1, pbest)  # Aplica la funcion por filas a la matriz.
    lbest = np.zeros((Utils.NUM_PARTICLES, dimensiones))

    mejor_valor = float("inf")
    mejor_posicion = np.ones(dimensiones)

    # Inicializar el entorno de cada partícula (comunicacion social)

    while t < 1000:
        t += 1
        for i in range(Utils.NUM_PARTICLES):
            valor = funcion(x[i, :])
            if valor < funcion(pbest[i, :]): # Encontrar el mínimo
                pbest[i, :] = x[i, :]
                best_valores[i] = valor

            if funcion(pbest[i, :]) < mejor_valor:
                mejor_valor = funcion(pbest[i, :])
                mejor_posicion = x[i, :]
                print(mejor_valor)
                print(mejor_posicion)

        for i in range(Utils.NUM_PARTICLES):
            # Escoger lbest[i], la partícula con mejor fitness del entorno de x[i]
            lbest[i, :] = min(np.apply_along_axis(funcion, 1, x[Utils.GetEntorno(i), :]))
            # Calcular v[i] de acuerdo a pbest[i] y lbest[i]
            # Calcular nueva posicion x[i]
            x[i], v[i] = Utils.GetNextPositionAndSpeed(Utils.OMEGA, v[i], Utils.PHI_1, pbest[i, :],
                                                       Utils.PHI_2, lbest[i, :], x[i, :])
    return x


x = Local_PSO(123456, 2, Utils.RastriginFunction)
