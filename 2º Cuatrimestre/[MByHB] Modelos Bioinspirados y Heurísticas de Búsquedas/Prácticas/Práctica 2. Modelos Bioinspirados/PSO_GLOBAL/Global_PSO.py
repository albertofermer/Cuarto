import numpy as np
import Utils


def Global_PSO(semilla, dimensiones, funcion):
    np.random.seed(semilla)
    t = 0
    # Inicializar X y V (Valores aleatorios entre MAX y MIN)
    x = (np.random.random(size=(Utils.NUM_PARTICLES, dimensiones))) * (Utils.MAX_POS - Utils.MIN_POS) + Utils.MIN_POS
    v = (np.random.random(size=(Utils.NUM_PARTICLES, dimensiones))) * (Utils.MAX_VEL - Utils.MIN_VEL) + Utils.MIN_VEL
    pbest = x.copy()  # Al principio, cada particula tiene como mejor posición ella misma.
    best_valores = np.apply_along_axis(funcion, 1, pbest)  # Aplica la funcion por filas a la matriz.
    gbest = np.ones((Utils.NUM_PARTICLES, dimensiones))*float('inf')
    mejor_valor = float("inf")
    mejor_posicion = np.ones(dimensiones)

    while t < 1000:
        t += 1
        for i in range(Utils.NUM_PARTICLES):
            valor = funcion(x[i, :])
            if valor < funcion(pbest[i, :]):    # Encontrar el mínimo
                pbest[i, :] = x[i, :]
                best_valores[i] = valor

            if funcion(pbest[i, :]) < funcion(gbest[i, :]):
                gbest[i, :] = pbest[i, :]

            if funcion(pbest[i, :]) < mejor_valor:
                mejor_valor = funcion(pbest[i, :])
                mejor_posicion = x[i, :]
                print(mejor_valor)
                print(mejor_posicion)

        for i in range(Utils.NUM_PARTICLES):
            # Escoger lbest[i], la partícula con mejor fitness de toda la nube (x)
            gbest[i, :] = min(np.apply_along_axis(funcion, 1, x))
            # Calcular v[i] de acuerdo a pbest[i] y lbest[i]
            # Calcular nueva posicion x[i]
            x[i], v[i] = Utils.GetNextPositionAndSpeed(Utils.OMEGA, v[i], Utils.PHI_1, pbest[i, :],
                                                       Utils.PHI_2, gbest[i, :], x[i, :])

    return x


x = Global_PSO(123456, 2, Utils.RosenbrockFunction)