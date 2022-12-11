====================================================================================
*													     *
*				Implementación del Algoritmo: AQ		     		     *
*													     *
*					Alberto Fernández Merchán				     *
*													     *
====================================================================================


***********************************************************************************	
*						EJECUCIÓN						    *
***********************************************************************************

Para ejecutar el algoritmo deberá seguir los siguientes pasos:

1. Abrir una consola de comandos
2. Situarse en el directorio donde se encuentra el fichero 
   "AQ.jar"
3. Escribir en la línea de comandos: 
------------------------------------------------------------------------------------
	java -jar AQ.jar <Ruta del Dataset> [Clase Positiva]
------------------------------------------------------------------------------------
	
	donde:
	- <Ruta del Dataset>: es la ruta del dataset que quiera evaluar
	- [Clase Positiva]: es el nombre de la clase que quiera considerar como 
				  positiva.
	
	En caso de no especificar [Clase Positiva] se tomará como positiva la clase 
	de la primera instancia del dataset.

	<> :  Obligatorio
	[] : Opcional

4. Darle a intro.


***********************************************************************************	
*							DATASETS					    *
***********************************************************************************
En la práctica se han incluido varios dataset de ejemplo en la carpeta llamada 
"Datasets".

Los datasets que se le pasen al algoritmo deberán tener cabecera y la clase deberá 
ser la última columna del dataset. Los ejemplos deberán ir cada uno en una línea y
 sus atributos delimitados con el símbolo ";".

A continuación se muestra un ejemplo de dataset:

TAMAÑO;COLOR;FORMA;CATEGORIA
SMALL;RED;CIRCLE;POSITIVE
LARGE;RED;CIRCLE;POSITIVE
SMALL;RED;TRIANGLE;NEGATIVE
LARGE;BLUE;CIRCLE;NEGATIVE


***********************************************************************************	
*							EJEMPLOS					    *
***********************************************************************************

java -jar AQ.jar .\..\Datasets\Dataset-Celulas.csv CANCERIGENA
java -jar AQ.jar .\..\Datasets\Dataset-Logica.csv 1
java -jar AQ.jar .\..\Datasets\Dataset-Animales.csv

