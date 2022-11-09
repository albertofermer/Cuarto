--- Comprueba la pertenencia de un número a una lista utilizando recursión de la
--- mayor cantidad de formas posibles.

--- Funciones Diferentes
pertenece1 :: Integer -> [Integer] -> Bool
pertenece1 n [] = False
pertenece1 n (x:xs) = if (n /= x) then pertenece1 n xs
			else (n == x)

--- Cláusulas guarda
pertenece2 :: Integer -> [Integer] -> Bool
pertenece2 n lista
	| (length lista) == 0 = False
	| (n == head lista) = True
	| otherwise = pertenece2 n (tail lista)

--- Cláusulas if-else
pertenece3 :: Integer -> [Integer] -> Bool
pertenece3 n lista = if (length lista) == 0 then False
			else if (n == head lista) then True
			else pertenece3 n (tail lista)

-- Cláusulas case
pertenece4 :: Integer -> [Integer] -> Bool
pertenece4 n lista = case (length lista) of
			0 -> False
			otherwise -> case (head lista) of
				n -> True
				otherwise -> pertenece4 n (tail lista)

