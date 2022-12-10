
------------ Declaración de los tipos de dato -----------------
type ListaIngredientes = [String]
type Precio = Float
data Pizza = Pizza { ingredientes :: ListaIngredientes, precio::Precio }deriving(Show)
--------------------------------------------------------------

--- Definición de las pizzas ------------------------------------------------------
margarita = Pizza {ingredientes = ["Tomate","Queso","Jamon York"], precio=10}
cuatroquesos = Pizza {ingredientes = ["Tomate","Roquefort","Gouda","Emmental","Mozzarella"], precio=12.5}
barbacoa = Pizza {ingredientes = ["Tomate", "Salsa BBQ", "Bacon", "Cebolla", "Ternera"], precio=14}
marinera = Pizza {ingredientes = ["Tomate", "Atun", "Anchoas","Aceitunas Negras"], precio=12.5}
campestre = Pizza {ingredientes = ["Tomate", "Champignones", "Pimiento", "Cebolla"], precio=12.5}
hawaiana = Pizza {ingredientes = ["Tomate", "Pina", "Maiz", "Jamon York"], precio= 13}
-------------------------------------------------------------------------------------

--- Creación de la Lista de Pizzas --------------------------------------------------
listaPizzas = [margarita,cuatroquesos,barbacoa,marinera,campestre,hawaiana]
-------------------------------------------------------------------------------------

--- Función DROPPRECIO --------------------------------------------------------------
dropPrecio :: Float -> [Pizza]
dropPrecio x = dropPrecioRec listaPizzas x

dropPrecioRec :: [Pizza] -> Float -> [Pizza]
dropPrecioRec [] _ = []
dropPrecioRec (cab:rest) x
	| (precio cab) > x = [cab] ++ dropPrecioRec rest x
	| otherwise = dropPrecioRec rest x
---------------------------------------------------------------------------------------