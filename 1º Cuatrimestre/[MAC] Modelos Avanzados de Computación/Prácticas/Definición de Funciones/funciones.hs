factorial::Integer->Integer
factorial 0 = 1
factorial n = n*factorial(n-1)

factorial_guarda::(Num a, Ord a) => a -> a
factorial_guarda n
	| n == 0 = 1
	| n > 0 = n*factorial_guarda(n-1)
	| otherwise = error "valor negativo"

signo::Integer->Bool
signo n
	| n >= 0 = True
	| n < 0 = False
	
factorialIF :: Num a => a->a
factorialIF n = if(n == 0) then 1 else n*factorialIF(n-1)

intervalo::Integer->Int
intervalo n = if(n < 25) then 1 
		else if ((n > 25) && (n<50)) then 2
		else if ((n > 50) && (n<75)) then 3
		else 4

--case_par::Integer->Integer
--case_par n = case (mod n 2 == 0) of
--		True->True
--		False->False

calculadora::Integer->Integer->Integer->Integer
calculadora n x y = case n of 
			1 -> x + y
			2 -> x - y


--elementos_lista::[Integer]->Integer
--elementos_lista lista = case lista of [] 	-> (-1)
--					(x:[]) 	-> 1

cuenta_elementos_lista::[Integer]->Integer
cuenta_elementos_lista [] = 0
cuenta_elementos_lista (x:xs) = 1 + cuenta_elementos_lista xs

-- Devuelve la lista de los sucesores
sucesores::[Integer]->[Integer]
sucesores [] = []
sucesores (x:xs) = [(x+1)]++sucesores xs

-- Devuelve una lista de positivos
elementos_positivos::[Integer]->[Integer]
elementos_positivos [] = []
elementos_positivos (x:xs)
	| x>=0 = x : elementos_positivos xs
	| otherwise = elementos_positivos xs

-- Redefinir la función until
--funcion_until::(Integer->Bool)->(Integer->Integer)->Integer->Integer--
--funcion_until p f x =
--	if p x then x
--	else funcion_until 




