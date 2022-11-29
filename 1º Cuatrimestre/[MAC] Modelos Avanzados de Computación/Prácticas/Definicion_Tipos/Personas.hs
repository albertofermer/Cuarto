data Persona = Persona {nombre :: String, apellidos :: String, edad :: Int} deriving(Show,Eq)
usuario1 = Persona {nombre = "n1", apellidos = "a11 a12", edad = 20}
usuario2 = Persona {nombre = "n2", apellidos = "a21 a22", edad = 25}
usuario3 = Persona {nombre = "n3", apellidos = "a31 a32", edad = 30}
usuario4 = Persona {nombre = "n4", apellidos = "a41 a42", edad = 40}
usuario5 = Persona {nombre = "n5", apellidos = "a51 a52", edad = 50}

personas = [usuario1, usuario2, usuario3, usuario4, usuario5]

takePersonas :: [Persona] -> Int -> [Persona]
takePersonas [] _ = []
takePersonas (p:ps) f = case (esMenor p f) of
			True -> [p]++takePersonas ps f
			otherwise -> takePersonas ps f

esMenor :: Persona -> Int -> Bool
esMenor (Persona _ _ e) e1 =  e < e1