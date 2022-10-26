get_mayor_abs :: (Num x, Ord x) => [x] -> x

get_mayor_abs  a = maximum (map abs a)