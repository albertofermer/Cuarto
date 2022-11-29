
data Fecha = Fecha Int Int Int deriving(Show)
data Prestamo = Prestamo Int String String Fecha Fecha String deriving(Show)

prestamo1 = (Prestamo 0 "Nombre" "FK_USUARIO" (Fecha 5 5 2006) (Fecha 4 2 1900) "OBS")
prestamo2 = (Prestamo 1 "Alberto" "usuario2" (Fecha 7 9 2017) (Fecha 4 2 2018) "OBS")
prestamo3 = (Prestamo 2 "Usuario2" "usuario3" (Fecha 5 5 2006) (Fecha 4 2 1900) "OBS")