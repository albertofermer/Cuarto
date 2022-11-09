--- Definir la sucesión de fibonacci de la mayor cantidad de formas dadas en clase:


--- Mediante clásulas guarda
fibonacci1 :: Integer -> Integer
fibonacci1 n 
	| n == 0 = 0
	| n == 1 = 1
	| otherwise = (fibonacci1 (n-1)) + (fibonacci1 (n-2))

--- Mediante cláusulas if-else
fibonacci2 :: Integer -> Integer
fibonacci2 n = if( n == 0 ) then 0
		else if (n == 1) then 1
		else (fibonacci2 (n-1)) + (fibonacci2 (n-2))

--- Mediante cláusulas case
fibonacci3 :: Integer -> Integer
fibonacci3 n = case n of
		0 -> 0
		1 -> 1
		otherwise -> (fibonacci3 (n-1)) + (fibonacci3 (n-2))

--- Mediante funciones 
fibonacci4 :: Integer -> Integer
fibonacci4 0 = 0
fibonacci4 1 = 1
fibonacci4 n = (fibonacci1 (n-1)) + (fibonacci1 (n-2))


	
