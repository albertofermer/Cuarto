filter2 :: (a -> Bool) -> [a] -> [a]
filter2 f lista = [x | x <- lista, f x]

-----  [(x,True) | x <- [1..20], even x, x<15 ]

