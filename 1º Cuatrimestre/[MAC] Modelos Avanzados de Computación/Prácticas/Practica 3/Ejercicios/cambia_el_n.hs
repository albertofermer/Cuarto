cambia_el_n :: Int -> [Int] -> Int -> [Int]

cambia_el_n  a b n = take (n) b++[a]++reverse (take (length b - (n+1)) (reverse b))