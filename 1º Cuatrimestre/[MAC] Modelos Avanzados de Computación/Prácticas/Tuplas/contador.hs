
contador [] = []
contador lista = [(length(filter (\x -> x == head (unico lista)) lista), head (unico lista))]++contador (tail lista)

-- (length(filter (\x -> x == 'a') "aaabcdef"), head (filter (\x -> x == x) "aaabcdef"))
unico [] = []
unico lista = (head lista):unico (filter (\x -> x /= (head lista)) lista)
