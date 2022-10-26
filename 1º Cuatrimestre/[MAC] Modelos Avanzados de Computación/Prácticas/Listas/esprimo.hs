esprimo :: Int -> Bool
esprimo x = length (divisores x) == 2

divisores :: Int -> [Int]
divisores a = filter (\z -> mod a z == 0 ) (take a (iterate (+1) 1))