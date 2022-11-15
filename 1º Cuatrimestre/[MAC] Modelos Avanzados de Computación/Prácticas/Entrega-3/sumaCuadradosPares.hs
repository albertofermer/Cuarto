sumaCuadradosPares :: [Integer] -> Integer
sumaCuadradosPares lista = sum [x^2 | x<- lista, even x ]