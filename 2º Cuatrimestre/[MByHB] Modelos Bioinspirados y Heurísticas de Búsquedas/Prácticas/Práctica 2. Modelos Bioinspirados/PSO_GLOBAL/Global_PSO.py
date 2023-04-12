import numpy as np
import Utils


def Global_PSO(semilla, funcion, vel, iteraciones_sin_mejora):
    np.random.seed(semilla)
    idfun = 0 if funcion == Utils.RosenbrockFunction else 1
    t = 0
    # Inicializar X y V (Valores aleatorios entre MAX y MIN)
    if funcion == Utils.RosenbrockFunction:
        _x = (np.random.uniform(Utils.MIN_POS_ROS_X, Utils.MAX_POS_ROS_X, Utils.NUM_PARTICLES))
        y = (np.random.uniform(Utils.MIN_POS_ROS_Y, Utils.MAX_POS_ROS_Y, Utils.NUM_PARTICLES))
        x = np.column_stack((_x, y))
    else:
        x = np.random.uniform(Utils.MIN_POS_RAS, Utils.MAX_POS_RAS, (Utils.NUM_PARTICLES, Utils.DIMENSION))

    v = np.random.uniform(vel[0], vel[1], (Utils.NUM_PARTICLES, Utils.DIMENSION))
    pbest = x.copy()  # Al principio, cada particula tiene como mejor posición ella misma.
    num_evaluaciones = len(pbest)
    best_valores = np.apply_along_axis(funcion, 1, pbest)  # Aplica la funcion por filas a la matriz.
    gbest = [10, 10]
    mejor_valor = float("inf")
    mejor_posicion = np.ones(Utils.DIMENSION)

    # Graficas
    mejores_posiciones = []
    mejores_valores = []
    coordenadas = x.copy()
    while t < iteraciones_sin_mejora:
        for i in range(Utils.NUM_PARTICLES):
            num_evaluaciones += 1
            valor = funcion(x[i, :])  # Evaluar X_i
            num_evaluaciones += 1
            pbest_valor = funcion(pbest[i, :])
            if valor < pbest_valor:  # Encontrar el mínimo
                pbest[i, :] = x[i, :].copy()
                best_valores[i] = valor
                num_evaluaciones += 1
                pbest_valor = funcion(pbest[i, :])

            num_evaluaciones += 1
            if pbest_valor < funcion(gbest):
                gbest = pbest[i, :].copy()

            if pbest_valor < mejor_valor:
                t = 0
                mejor_valor = pbest_valor
                mejor_posicion = x[i, :].copy()
                # print(mejor_valor)
                # print(mejor_posicion)

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


if __name__ == "__main__":
    # Utils.generarVideos(Global_PSO)
    Utils.graficas_resultados(Global_PSO)

    # np.set_printoptions(suppress=True)  # Para quitar la notación científica
    # mejor_ros, mejor_ras = Utils.experimentacion(Global_PSO)
    # print(f"ROSENBROCK:\n {mejor_ros}")
    # print(f"RASTRIGIN:\n {mejor_ras}")

