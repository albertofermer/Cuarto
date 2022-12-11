------------ Declaración de los tipos de dato --------------------------------
data Fecha = Fecha {dia::Integer, mes::Integer, agno::Integer}deriving(Eq,Show)
data Persona = Persona {nombre::String, apellidos::String, nacimiento::Fecha}deriving(Show)
------------------------------------------------------------------------------

----------------------------- Creacion de las Personas ----------------------------------------
persona1 = Persona {nombre="Alberto",apellidos="Fernandez Merchan",nacimiento=(Fecha 29 3 2001)}
persona2 = Persona {nombre="Alba",apellidos="Marquez Rodriguez",nacimiento=(Fecha 7 5 2001)}
persona3 = Persona {nombre="Laura",apellidos="Fernandez Merchan",nacimiento=(Fecha 13 8 1997)}
persona4 = Persona {nombre="Pepe",apellidos="Castilla Rodriguez",nacimiento=(Fecha 20 4 1975)}
persona5 = Persona {nombre="Antonio", apellidos="Fernandez Rodriguez",nacimiento=(Fecha 16 8 1950)}
--------------------------------------------------------------------------------------------------
-------------------------------------- Lista de Personas -----------------------------------------
listapersonas = [persona5, persona4, persona3, persona1, persona2]
--------------------------------------------------------------------------------------------------

------------------------------ Funciones ---------------------------------------------------------

--- Funciones para comparar fechas ---
esMenorFecha :: Fecha -> Fecha -> Bool
esMenorFecha fecha1 fecha2 = ((agno(fecha1)*10000) + (mes(fecha1)*100) + dia(fecha1)) < ((agno(fecha2)*10000) + (mes(fecha2)*100) + dia(fecha2))

esMayorFecha :: Fecha -> Fecha -> Bool
esMayorFecha fecha1 fecha2 = ((agno(fecha1)*10000) + (mes(fecha1)*100) + dia(fecha1)) > ((agno(fecha2)*10000) + (mes(fecha2)*100) + dia(fecha2))
------------------------------------------

--- Funcion para obtener las personas que han nacido en cierta fecha. ---
getListDate :: Fecha -> String -> [Persona]
getListDate fecha criterio  = case criterio of
				"antes"   -> [x| x<-listapersonas, esMenorFecha (nacimiento x) fecha]
				"despues" -> [x| x<-listapersonas, esMayorFecha (nacimiento x) fecha]
				"misma"   -> [x| x<-listapersonas, (nacimiento x) == fecha]
				otherwise -> error "Criterio no reconocido"