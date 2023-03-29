import numpy as np
import math
import matplotlib.pyplot as plt

'''
Valores que se utilizarán en el algoritmo PSO
'''
SEEDS = [123456, 234561, 345612, 456123, 561234]
MAX_POS = 10
MIN_POS = -MAX_POS

# Constantes para PSO
NUM_PARTICLES = 10
VECINDAD = 2
OMEGA = 0.729
PHI_1 = 1.49445
PHI_2 = 1.49445
MAX_VEL = 0.01
MIN_VEL = -MAX_VEL
# Constantes para Busqueda Local
GRANULARIDAD = 0.1
NUM_VECINOS = 10


def RosenbrockFunction(x):
    return (1 - x[0]) ** 2 + 100 * (x[1] - x[0] ** 2) ** 2


def RastriginFunction(x):
    return 20 + x[0] ** 2 + x[1] ** 2 - 10 * (np.cos(2 * math.pi * x[0]) + np.cos(2 * math.pi * x[1]))


'''
Calcula la siguiente posición de una partícula.
'''


def GetNextPositionAndSpeed(omega, v, phi_1, pbest, phi_2, g, pos):
    next_pos = np.zeros(len(pos))
    next_v = np.zeros(len(pos))
    for i in range(len(pos)):  # Para cada dimension del vector posicion,
        next_v[i] = omega * v[i] + phi_1 * np.random.random() * (pbest[i] - pos[i]) + phi_2 * np.random.random() * (
                g[i] - pos[i])

        if next_v[i] >= MAX_VEL:
            next_v[i] = MAX_VEL
        elif next_v[i] <= MIN_VEL:
            next_v[i] = MIN_VEL

        next_pos[i] = pos[i] + next_v[i]

        # Si se sale de los límites del tablero
        if next_pos[i] >= MAX_POS:
            next_pos[i] = MAX_POS
        elif next_pos[i] <= MIN_POS:
            next_pos[i] = MIN_POS

    return next_pos, next_v


def GetEntorno(indice_particula):
    return [(indice_particula + i) % NUM_PARTICLES for i in range(-VECINDAD, VECINDAD + 1) if i != 0]


'''
x -> [float, float]
suma -> [bool, bool]

Si suma[i] -> x[i] += granularidad
sino: x[i] -= granularidad

'''


def GenerarVecino(x, suma):
    vecino = x.copy()
    for i in range(len(suma)):
        if suma[i]:
            vecino[i] += GRANULARIDAD
            if vecino[i] > MAX_POS:
                vecino[i] = MAX_POS
        else:
            vecino[i] -= GRANULARIDAD
            if vecino[i] < MIN_POS:
                vecino[i] = MIN_POS
    return vecino

def DrawParticle(x):
    fig, ax = plt.plot()
    ax.scatter(x[0],x[1])
    plt.draw()

if __name__ == "__main__":
    # sol_inicial = np.array([1, 1], dtype=float)
    # s_act = sol_inicial
    # for _ in range(10):
    #     # TODO: for para sacar todas las combinaciones de suma -> [bool, bool]
    #     s_act = GenerarVecino(s_act, [True, True])
    #     print(s_act)
    print(GetEntorno(2))
