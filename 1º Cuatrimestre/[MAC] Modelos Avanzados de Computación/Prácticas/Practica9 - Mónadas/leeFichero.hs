import IO
import Char

main = 	do

	hdl <- openFile "C:\\Users\\afmhu\\OneDrive - UNIVERSIDAD DE HUELVA\\4º Curso\\1º Cuatrimestre\\[MAC] Modelos Avanzados de Computación\\Prácticas\\Practica9 - Mónadas\\FicheroA.txt" ReadMode
	lee hdl

lee hdl = do t <- hIsEOF hdl
	if t then return()
		else do y <- hGetLine hdl
	putStrLn y
	lee hdl