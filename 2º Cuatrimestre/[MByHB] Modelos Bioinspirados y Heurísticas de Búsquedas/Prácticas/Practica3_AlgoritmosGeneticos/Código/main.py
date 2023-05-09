import Basico.Algoritmo_Genetico_Basico as basico
import CHC.Algoritmo_Genetico_CHC as chc
import Multimodal.Algoritmo_Genetico_Multimodal as mm
import Utils as utils
import sys

if __name__ == "__main__":
    name_to_value = {"basico": 0, "chc": 1, "multimodal": 2}
    if len(sys.argv) < 3:
        raise Exception("Debe escribir un argumento en la llamada.\n"
                        f"\tEl primero corresponde con el nombre del algoritmo. \n"
                        f"['basico', 'chc', 'multimodal']\n"
                        f"\tEl segundo con la semilla.\n"
                        f"\tEl tercero si corresponde a los datos aleatorios o reales (1, 0)"
                        )

    name = sys.argv[1]
    seed = int(sys.argv[2])
    isRandom = True if int(sys.argv[3]) == 1 else False
    x = -1
    if name in name_to_value and name_to_value[name] == 0:  # Genetico Basico
        x = basico.algoritmo_genetico_generacional(seed, isRandom)[0]
    elif name in name_to_value and name_to_value[name] == 1:  # CHC
        x = chc.CHC(seed, isRandom)[0]
    elif name in name_to_value and name_to_value[name] == 2:  # Multimodal
        x = mm.algoritmo_genetico_generacional_multimodal(seed, isRandom)[0]
    else:
        print("No se ha encontrado el algoritmo.")

    print(f"{round(x/100,2)} euros.")
