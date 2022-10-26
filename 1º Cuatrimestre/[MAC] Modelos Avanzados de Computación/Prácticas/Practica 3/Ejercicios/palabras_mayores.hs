palabras_mayores :: Int -> [String] -> [String]

palabras_mayores  n list = filter (\x -> length x > n) list