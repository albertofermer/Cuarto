-- Sea la función nsobrek tal que nsobrek n k es el número de combinaciones de n 
-- elementos tomados de k en k.
-- Definir en Haskell en el mayor número de variantes que hemos visto.

-- Utilizando la cláusula where podemos definir una función en el interior de 
-- la propia función
nsobrek1 :: Integer -> Integer -> Integer
nsobrek1 n k = div (factorial n) ((factorial k)*factorial (n-k))
	where 
	factorial 0 = 1
	factorial x = x*(factorial (x-1))

-- Utilizando la cláusula where para definir la función factorial de diferentes formas. 
--- Con guardas:
nsobrek2 :: Integer -> Integer -> Integer
nsobrek2 n k = div (factorial n) ((factorial k)*factorial (n-k))
	where
	factorial n 
		| n == 0 = 1
		| n > 0 = n*(factorial (n-1))
		| otherwise = 0

--- Con if-else
nsobrek3 :: Integer -> Integer -> Integer
nsobrek3 n k = div (factorial n) ((factorial k)*factorial (n-k))
	where
	factorial n = if(n==0) then 1
			else if (n > 0) then n*factorial(n-1)
			else 0
			
--- Con cláusula switch
nsobrek4 :: Integer -> Integer -> Integer
nsobrek4 n k = div (factorial n) ((factorial k)*factorial (n-k))
	where
	factorial n = case n of
			0->1
			otherwise->n*factorial(n-1)
			
--- Definiendo la función factorial fuera del entorno local
nsobrek5 :: Integer -> Integer -> Integer
nsobrek5 n k = div (factorial5 n) ((factorial5 k)*factorial5 (n-k))

factorial5 0 = 1
factorial5 x = x*factorial5(x-1)