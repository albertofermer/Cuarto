
------------ Declaración de los tipos de dato -----------------
type ListaIngredientes = [String]
type Precio = Float
data Pizza = Pizza { ingredientes :: ListaIngredientes, precio::Precio }deriving(Eq,Show)
--------------------------------------------------------------

--- Definición de las pizzas --------------------------------------------------------
margarita = Pizza {ingredientes = ["Tomate","Queso","Jamon York"], precio=10}
cuatroquesos = Pizza {ingredientes = ["Tomate","Roquefort","Gouda","Emmental","Mozzarella"], precio=12}
barbacoa = Pizza {ingredientes = ["Tomate", "Salsa BBQ", "Bacon", "Cebolla", "Ternera"], precio=16}
marinera = Pizza {ingredientes = ["Tomate", "Atun", "Anchoas","Aceitunas Negras"], precio=13}
campestre = Pizza {ingredientes = ["Tomate", "Champignones", "Pimiento", "Cebolla"], precio=9}
hawaiana = Pizza {ingredientes = ["Tomate", "Pina", "Maiz", "Jamon York"], precio= 12}
---------------------------------------------------------------------------------------

--- Creación de la Lista de Pizzas ----------------------------------------------------
listaPizzas = [margarita,cuatroquesos,barbacoa,marinera,campestre,hawaiana]
---------------------------------------------------------------------------------------

--- Obtiene la lista de pizzas con un precio superior a X
precioSuperiorA :: Precio -> [Pizza] -> [Pizza]
precioSuperiorA _ [] = []
precioSuperiorA x (cab:rest)
	| precio(cab) >= x = [cab]++(precioSuperiorA x rest)
	| otherwise = (precioSuperiorA x rest)

-- Obtiene la pizza con menor precio de una lista
getMenorPrecio :: [Pizza] -> Pizza
getMenorPrecio (cab:[]) = cab
getMenorPrecio (cab:(cab2:rest))
	| precio(cab) <= precio(cab2) = getMenorPrecio ([cab]++rest)
	| precio(cab2) < precio(cab) = getMenorPrecio ([cab2]++rest)

-- Obtiene el segmento que comienza con la pizza que tiene menor precio superior a X euros.
-- Llama a la función para que acepte un parámetro tipo lista de pizzas.
dropPrecio :: Precio -> [Pizza]
dropPrecio x = dropPrecioRec x listaPizzas

-- Obtiene el segmento que comienza con la pizza que tiene menor precio superior a X euros.
dropPrecioRec :: Precio -> [Pizza] -> [Pizza]
dropPrecioRec _ [] = []
dropPrecioRec x (cab:rest) 
	| cab==getMenorPrecio(precioSuperiorA x ([cab]++rest)) = [cab]++rest
	| otherwise = dropPrecioRec x rest
