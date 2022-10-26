get_pos::[Int]->Int->Int --Cabecera

get_pos a n = length a - length(dropWhile(\x -> x /= n) a)