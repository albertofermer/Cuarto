import BEST_SEARCH.BestSearch as best
import PSO_GLOBAL.Global_PSO as psoglobal
import PSO_LOCAL.Local_PSO as psolocal
import Utils as utils
import sys

if __name__ == "__main__":
    name_to_value = {"best": 0, "local": 1, "global": 2}
    if len(sys.argv) < 2:
        raise Exception("Debe escribir un argumento en la llamada.\n"
                        f"\tEl primero corresponde con el nombre del algoritmo. "
                        f"['best', 'local', 'global']\n"
                        f"En caso de utilizar la búsqueda local debe insertar también"
                        f"el identificador de la función ([0 (Rosenbrock) 1 (Rastrigin)])")

    name = sys.argv[1]

    if name in name_to_value and name_to_value[name] == 0:  # Busqueda El Mejor
        print("Este algoritmo no necesita semilla, ni velocidad ni genera gráficas.")
        funcion = utils.RosenbrockFunction if int(sys.argv[2]) == 0 else utils.RastriginFunction
        x = best.busqueda_elmejor(funcion)
        print(x)
    elif name in name_to_value and name_to_value[name] == 1:  # PSO Local
        utils.graficas_resultados(psolocal.Local_PSO)
    elif name in name_to_value and name_to_value[name] == 2:  # PSO Global
        utils.graficas_resultados(psoglobal.Global_PSO)
    else:
        print("No se ha encontrado el algoritmo.")
