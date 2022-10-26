unico :: (Eq a) => [a] -> [a]

unico [] = []
unico lista = (head lista):unico (filter (\x -> x /= (head lista)) lista)
