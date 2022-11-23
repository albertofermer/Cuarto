module MiModulo  
( generaProximo,
busca_numero
) where  
import Data.Char -- Módulo para trabajar con los caracteres
generaProximo :: Int -> Int -> Int
generaProximo a b = div (a + b) 2

-- Declaramos la función que ejecuta el juego.
-- a: el número minimo
-- b: el número máximo
busca_numero :: Int -> Int -> IO()
busca_numero a b = do 
			-- El número que pregunta será el que se encuentre a la mitad del intervalo
			let proximo = generaProximo a b 
			-- Muestra por pantalla el mensaje
			putStrLn $ "Es el " ++ (show proximo) ++ "?" 
			-- Obtiene la respuesta del usuario
			respuesta <- getLine 		
			-- La convierte en minúsculas			
			let resp = map toLower respuesta
			
			-- Si a = b entonces solo hay una opción.
			if (a == b) then putStrLn $ "La unica opcion es: " ++ (show proximo) ++ "."
			-- Si es el número, entonces termina
			else if (resp == "encontrado") then	 
				return ()
			-- Si es mayor, llama a la función con el intervalo mayor.
			else if (resp == "mayor") then	
				busca_numero proximo b
			-- Si es menor, llama a la función con el intervalo menor.
			else if (resp == "menor") then  
				busca_numero a proximo
			-- En otro caso se considera un error y se llama a la funcion
			-- con los mismos parámetros.
			else				
				do			
					print $ "Error"
					busca_numero a b