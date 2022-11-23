main = do
	putStrLn "Hola"
	name <- getLine
	putStrLn ("Hola, " ++ name)