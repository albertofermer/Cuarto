mult_positiva :: [Integer] -> Integer

mult_positiva a = foldl (\x y -> x * y) 1 (filter (> 0) a)