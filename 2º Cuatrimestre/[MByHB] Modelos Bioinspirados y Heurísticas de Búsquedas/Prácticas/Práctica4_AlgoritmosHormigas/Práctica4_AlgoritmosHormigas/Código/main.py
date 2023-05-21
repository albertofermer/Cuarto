from SistemasDeHormigas import SistemaHormigas
from SistemaHormigasElitista import SistemaHormigasElitista
from Greedy import greedy
import Utils
import sys

if __name__ == "__main__":
    name_to_value = {"greedy": 0, "SH": 1, "SHE": 2}
    if len(sys.argv) < 3:
        raise Exception("Debe escribir un argumento en la llamada.\n"
                        f"\tEl primero corresponde con el nombre del algoritmo. \n"
                        f"['greedy', 'SH', 'SHE']\n"
                        f"\tEl segundo con la semilla.\n"
                        f"\tEl tercero el nombre del problema [ch130. a280]\n"
                        )

    name = sys.argv[1]
    seed = int(sys.argv[2])
    problema = sys.argv[3]
    # isRandom = True if int(sys.argv[3]) == 1 else False
    x = -1
    if name in name_to_value and name_to_value[name] == 0:  # Greedy
        x = greedy.tsp_greedy(seed,problema)[0]
    elif name in name_to_value and name_to_value[name] == 1:  # SH
        x = SistemaHormigas.SistemaHormigas(seed, problema)[0]
    elif name in name_to_value and name_to_value[name] == 2:  # SHE
        x = SistemaHormigasElitista.SistemaHormigasElitista(seed, problema)[0]
    else:
        print("No se ha encontrado el algoritmo.")

    print(f"Coste: {x}")
