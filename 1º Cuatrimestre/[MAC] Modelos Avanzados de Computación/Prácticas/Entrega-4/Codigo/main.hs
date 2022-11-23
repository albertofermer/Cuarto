-- Importamos los módulos que vamos a utilizar
import MiModulo -- Módulo local

-- Declaramos la función principal
main :: IO() 

main = do
	putStrLn $ "Piensa un numero del 1 al 100"
	busca_numero 1 100 -- Llama a la función de buscar el numero entre el 1 y el 100 del modulo "MiModulo".
	putStrLn $ "FIN DEL JUEGO"