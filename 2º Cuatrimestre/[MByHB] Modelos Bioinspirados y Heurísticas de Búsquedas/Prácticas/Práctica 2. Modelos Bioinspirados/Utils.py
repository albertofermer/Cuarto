import numpy as np
import math

'''
Valores que se utilizarán en el algoritmo PSO
'''
SEEDS = [123456, 234561, 345612, 456123, 561234]
MAX_POS = 10
MIN_POS = -MAX_POS

# Constantes para PSO
NUM_PARTICLES = 1000
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
    return (1 - x[0])**2 + 100*(x[1] - x[0]**2)**2


def RastriginFunction(*X, **kwargs):
    A = kwargs.get('A', 10)
    return A + sum([(x ** 2 - A * np.cos(2 * math.pi * x)) for x in X])


'''
Calcula la siguiente posición de una partícula.
'''


def GetNextPositionAndSpeed(omega, v, phi_1, pbest, phi_2, g, pos):
    next_pos = np.zeros(len(pos))
    next_v = np.zeros(len(pos))
    for i in range(len(pos)):  # Para cada dimension del vector posicion,
        next_v[i] = omega * v[i] + phi_1 * np.random.random() * (pbest[i] - pos[i]) + phi_2 * np.random.random() * (g[i] - pos[i])

        if next_v[i] >= MAX_VEL:
            next_v[i] = MAX_VEL
        elif next_v[i] <= MIN_VEL:
            next_v[i] = MIN_VEL

        next_pos[i] = pos[i] + next_v[i]

        if next_pos[i] >= MAX_POS:
            next_pos[i] = MAX_POS
        elif next_pos[i] <= MIN_POS:
            next_pos[i] = MIN_POS

    #print(next_pos)
    return next_pos, next_v


def GetEntorno(indice_particula):
    return [(indice_particula + i) % NUM_PARTICLES for i in range(-VECINDAD, VECINDAD + 1) if i != 0]


if __name__ == "__main__":
    print(GetEntorno(0))
