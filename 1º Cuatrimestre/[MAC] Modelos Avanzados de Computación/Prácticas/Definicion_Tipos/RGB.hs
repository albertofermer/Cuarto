data RGB = RGB Int Int Int deriving(Show)

mezclar :: RGB -> RGB -> RGB
mezclar (RGB a b c) (RGB d e f) = (RGB a+d b+e c+f) 
 