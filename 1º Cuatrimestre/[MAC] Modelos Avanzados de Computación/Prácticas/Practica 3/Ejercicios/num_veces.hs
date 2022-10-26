num_veces :: Int -> [Int] -> Int

num_veces  a b = length (filter (==a) b)