-------------------------------------------------------- Declaración del tipo Arbol Binario -----------------------------------------------------------------
data ArbolBinario = Vacio | Hoja {valor::Int} | Nodo {valor::Int, izq::ArbolBinario, der::ArbolBinario} deriving(Eq,Show)
-------------------------------------------------------------------------------------------------------------------------------------------------------------

--------------------------------------------------------------------- Declaración del arbol -----------------------------------------------------------------
arbol = Nodo{valor=0, izq=(Nodo{valor=1,
				izq=(Nodo{valor=3,
					izq=(Hoja{valor=7}),
					der=Vacio}),
				der=(Nodo{valor=4,
					izq=(Hoja{valor=8}),
					der=Vacio})}),
		      der=(Nodo{valor=2, 
				izq=(Nodo{valor=5,
					izq=(Hoja{valor=9}),
					der=Vacio}), 
				der=(Nodo{valor=6,
					izq=(Hoja{valor=10}),
					der=(Hoja{valor=11})})})}

-------------------------------------------------------------------------------------------------------------------------------------------------------------


------------------------------------------------------------------------ Funcion de recorrido Inorden--------------------------------------------------------
-- 					Devuelve una lista con los valores de los nodos en inorden (izquierda, actual, derecha)				   --
-------------------------------------------------------------------------------------------------------------------------------------------------------------
recorridoInorden :: ArbolBinario -> [Int]
recorridoInorden Vacio = []
recorridoInorden (Hoja x) = [x]
recorridoInorden (Nodo valor izq der) =  (recorridoInorden izq) ++ [valor] ++ (recorridoInorden der)
-------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------ Funcion de recorrido Postorden -----------------------------------------------------
-- 					Devuelve una lista con los valores de los nodos en postorden (izquierda, derecha, actual)			   --
-------------------------------------------------------------------------------------------------------------------------------------------------------------
recorridoPostorden :: ArbolBinario -> [Int]
recorridoPostorden Vacio = []
recorridoPostorden (Hoja x) = [x]
recorridoPostorden (Nodo valor izq der) =  (recorridoPostorden izq) ++ (recorridoPostorden der) ++ [valor]
-------------------------------------------------------------------------------------------------------------------------------------------------------------


------------------------------------------------------------------------ Funcion de recorrido Preorden ------------------------------------------------------
-- 						Devuelve una lista con los valores de los nodos en preorden (actual, izquierda, derecha) 	  	   --
-------------------------------------------------------------------------------------------------------------------------------------------------------------
recorridoPreorden :: ArbolBinario -> [Int]
recorridoPreorden Vacio = []
recorridoPreorden (Hoja x) = [x]
recorridoPreorden (Nodo valor izq der) =  [valor] ++ (recorridoPreorden izq) ++ (recorridoPreorden der)
-------------------------------------------------------------------------------------------------------------------------------------------------------------


------------------------------------------------------------------------ Funcion nivelArbol -----------------------------------------------------------------
-- 					Devuelve una lista con los valores de los nodos del nivel pasado por parámetro a la función.			   --
-------------------------------------------------------------------------------------------------------------------------------------------------------------

nivelArbol :: ArbolBinario -> Int -> [Int]
nivelArbol Vacio _ = []
nivelArbol (Nodo valor izq der) 0 = [valor]
nivelArbol (Hoja x) nivel = [x]
nivelArbol (Nodo valor izq der) nivel = (nivelArbol izq (nivel-1))++(nivelArbol der (nivel-1))
-------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------ Funcion alturaArbol ----------------------------------------------------------------
-- 								    Devuelve la altura del árbol							   --
-------------------------------------------------------------------------------------------------------------------------------------------------------------
alturaArbol :: ArbolBinario -> Int
alturaArbol (Hoja _) = 1
alturaArbol (Nodo _ izq Vacio) = 1 + (alturaArbol izq)
alturaArbol (Nodo _ Vacio der) = 1 + (alturaArbol der)
alturaArbol (Nodo _ izq der) = 1 + (max (alturaArbol izq) (alturaArbol der))
-------------------------------------------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------ Funcion de recorrido en Anchura ----------------------------------------------------
-- 					Devuelve una lista con los valores de los nodos del árbol recorridos en anchura (de izquierda a derecha) 	   --
-------------------------------------------------------------------------------------------------------------------------------------------------------------
recorridoAnchura :: ArbolBinario -> [Int]
recorridoAnchura arbol = recorridoAnchuraRec arbol ((alturaArbol arbol)-1)
-------------------------------------------------------------------------------------------------------------------------------------------------------------

---------------------------------------------------------------- Funcion de recorrido en Anchura Recursiva --------------------------------------------------
-- 					Devuelve una lista con los valores de los nodos del árbol recorridos en anchura (de izquierda a derecha) 	   --
--					Concatena los valores de cada nivel del árbol desde el nivel 0 hasta el nivel (alturaArbol - 1)			   --
-------------------------------------------------------------------------------------------------------------------------------------------------------------
recorridoAnchuraRec :: ArbolBinario -> Int -> [Int]
recorridoAnchuraRec arbol 0 = nivelArbol arbol 0
recorridoAnchuraRec arbol nivel = (recorridoAnchuraRec arbol (nivel-1))++(nivelArbol arbol nivel)
-------------------------------------------------------------------------------------------------------------------------------------------------------------


---------------------------------------------------------------- Funcion Principal de Recorridos ------------------------------------------------------------
-- 					Devuelve una lista con los nodos del árbol recorridos según el criterio que se le pase por parámetro 		   --
-------------------------------------------------------------------------------------------------------------------------------------------------------------
recorreArbol :: ArbolBinario -> String -> [Int]
recorreArbol arbol criterio = case criterio of
				"inorden" -> recorridoInorden arbol
				"preorden" -> recorridoPreorden arbol
				"postorden" -> recorridoPostorden arbol
				"anchura" -> recorridoAnchura arbol
				otherwise -> error "Recorrido no reconocido"
---------------------------------------------------------------------------------------------------------------------------------------
