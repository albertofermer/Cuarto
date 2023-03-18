import sys
import time

# Inicio del temporizador
start_time1 = time.perf_counter()
var = [0]*10000
end_time1 = time.perf_counter()

start_time2 = time.perf_counter()
var2 = [0 for _ in range(10000)]
end_time2 = time.perf_counter()

# Tiempo transcurrido
elapsed_time1 = end_time1 - start_time1
elapsed_time2 = end_time2 - start_time2
print("El tiempo de ejecución fue de: ", elapsed_time1, "segundos.")
print("El tiempo de ejecución fue de: ", elapsed_time2, "segundos.")