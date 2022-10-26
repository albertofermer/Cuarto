es_palindromo :: [String] -> [String] --- Cabecera

es_palindromo a = filter (\x -> reverse x == x) a