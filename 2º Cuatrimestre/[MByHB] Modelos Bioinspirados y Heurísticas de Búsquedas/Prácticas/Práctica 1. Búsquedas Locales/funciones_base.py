import constantes
import random

# Carga los datos de precio de venta, de compra y de radiacion de cada uno de los
# dos problemas: True -> Problema Aleatorio.

# Constantes
capacidad_bateria = constantes.capacidad_bateria


def get_vectores(israndom):
    if israndom:
        precio_venta = constantes.precio_venta_random
        precio_compra = constantes.precio_compra_random
        r = constantes.r_random
    else:
        precio_venta = constantes.precio_venta
        precio_compra = constantes.precio_compra
        r = constantes.r
    return precio_venta, precio_compra, r


'''
-----------------------------------------------------------------------------------------------------------
La funcion almacenar_bateria se encarga de implementar la logica que hay detrás de la accion de almacenar
energia dentro de la bateria. 
-----------------------------------------------------------------------------------------------------------
Pueden ocurrir dos casos:

1. Almacena en la bateria toda la energia_disponible.
2. Almacena en la bateria la eneria disponible hasta llenarla y vende el sobrante.
Por último, en caso de que quede energia disponible, se vende.

Devuelve el estado de la bateria, la energia disponible y el dinero generado.

'''


def almacenar_bateria(bateria, energia_generada, dinero, i, israndom):
    precio_venta, precio_compra, r = get_vectores(israndom)
    # En caso de que quepa toda la energia disponible en la bateria.
    if bateria < capacidad_bateria and energia_generada <= (capacidad_bateria - bateria):
        bateria += energia_generada  # En KWh
        energia_generada = 0
    # En caso de que no quepa toda la energia disponible en la bateria, pero la bateria no esté llena:
    elif bateria < capacidad_bateria and energia_generada > (capacidad_bateria - bateria):
        energia_generada = energia_generada - (capacidad_bateria - bateria)  # En KWh
        bateria = capacidad_bateria

    # En caso de que la bateria esté llena bateria == capacidad_bateria, la vende
    # En caso de que sobre energia_disponible, la vende toda.
    dinero += energia_generada * precio_venta[i]
    energia_generada = 0

    return bateria, energia_generada, dinero


'''
La funcion generar_inicial genera una solucion inicial válida.
En mi caso, generará un vector de 24 posiciones cuyos valores indicarán:

Si es >= 0: Venta de Energía.
Si es < 0: Compra de Energía.

La granularidad será el paso de cada porcentaje. Estudiaremos tres tipos:
    Granularidad = 1 : 0, 1, 2, 3 ... 100
    Granularidad = 5 : 0, 5, 10, 15 ... 100
    Granularidad = 10: 0, 10, 20, 30 ... 100
'''


# Generador de la solucion Inicial
def generar_inicial(semilla, longitud_vector, granularidad):
    random.seed(semilla)
    solucion_inicial = [random.randrange(-100, 101, granularidad) for _ in range(longitud_vector)]

    return solucion_inicial


def vender(bateria, hora, solucion, dinero, israndom):
    precio_venta, precio_compra, r = get_vectores(israndom)
    # Vender
    energia_disponible = bateria + r[hora] * 0.2  # En KWh
    energia_vendida = abs(solucion[hora]) / 100 * energia_disponible  # En KWh
    dinero += energia_vendida * precio_venta[hora]  # En KWh

    if bateria >= energia_vendida:  # Si hay suficiente energia para vender en la bateria
        bateria -= energia_vendida  # Vende de la bateria
        energia_disponible = bateria + r[hora] * 0.2

    else:  # En otro caso
        energia_disponible -= energia_vendida  # Vendo toda la bateria y lo que falte de lo que haya generado.
        bateria = 0

    # Almacena en la bateria lo que sobre y vende lo que no quepa en la bateria
    bateria, energia_disponible, dinero = almacenar_bateria(bateria, energia_disponible - bateria, dinero, hora, israndom)

    return bateria, energia_disponible, dinero


'''
La funcion de evaluacion simula cuanto dinero genera una solucion.
Contiene un bucle que recorre el vector completo y para cada valor comprueba si:
    Si es >= 0: 
        Vende un porcentaje de lo que haya generado y lo que contenga la bateria.
        También almacena lo que no haya vendido en la batería y, si al almacenar
        llena la batería, vende la energía sobrante.
        
    En otro caso:
        Compra un porcentaje de lo que haya y almacena lo que haya generado ese día. Si al almacenar llena la batería,
        vende la energía sobrante.
    
    Finalmente, vende todo lo que haya en la batería y sobre.
    
'''


# Funcion de Coste
def funcion_evaluacion(solucion, israndom):
    precio_venta, precio_compra, r = get_vectores(israndom)
    bateria = 0  # 1 -- 300 kWh
    dinero = 0  # en centimos
    # Listas para sacar los datos de las graficas
    dinero_acumulado = [0 for _ in range(24)]
    bateria_hora = [0 for _ in range(24)]
    energia_disponible_hora = [0 for _ in range(24)]

    for hora in range(24):
        if solucion[hora] >= 0:

            bateria, energia_disponible, dinero = vender(bateria, hora, solucion, dinero, israndom)
        else:
            # Comprar
            # Aunque hayamos decidido comprar, también generamos cierta energía ese día:
            energia_disponible = bateria + r[hora] * 0.2  # En KWh

            # Almacena y vende el sobrante
            bateria, energia_disponible, dinero = almacenar_bateria(bateria, energia_disponible - bateria, dinero, hora, israndom)

            # Compro un porcentaje de lo que me sobra de bateria. De esta forma no podemos sobrepasar el limite de la
            # bateria en ningun momento.
            energia_comprada = solucion[hora] / 100 * (capacidad_bateria - bateria)  # (Sale negativo)
            dinero += energia_comprada * precio_compra[hora]  # Como energia_comprada es < 0 entonces se resta.

            bateria += -energia_comprada

            # Si la energia comprada es 0, entonces ¿vendo?
            if energia_comprada == 0:
                bateria, energia_disponible, dinero = vender(bateria, hora, solucion, dinero, israndom)

        # Guardamos los datos para las gráficas
        energia_disponible_hora[hora] = energia_disponible
        dinero_acumulado[hora] = dinero
        bateria_hora[hora] = bateria

    # Finalmente, vendemos toda la energia almacenada en la bateria
    dinero += bateria * precio_venta[23]
    bateria = 0

    dinero_acumulado[23] = dinero
    bateria_hora[23] = bateria

    return dinero, dinero_acumulado, bateria_hora

