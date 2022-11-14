function H = funcion_calcula_mascara_gaussiana_eficiente(W,sigma,representa)
%-------------------------------------------------------------------------%
% Es un filtro lineal ponderado que intenta imitar la forma de una
% distribución gaussiana de media 0 y varianza sigma^2.
%
% Se obtiene discretizando una distribución gaussiana continua de dos
% dimensiones.
%
% A medida que el valor de sigma aumenta, los términos de la función
% gaussiana alejados de la media son mayores y no deben ser despreciados.
% Por ello, debemos elegir un tamaño de W adecuado a la sigma que
% utilicemos. Esta relación es de W = 5 * sigma.
%
% Si utilizamos una ventana demasiado pequeña, no estaríamos aprovechando
% toda la campana de gauss y si la eligiésemos demasiado grande
% obtendríamos puntos donde la máscara es 0.
%-------------------------------------------------------------------------%


[X, Y] = meshgrid(-floor(W/2):floor(W/2));
H_aux = exp( (-1/2)*((X.^2+Y.^2)/sigma^2));
H = ( 1/(sum(H_aux(:))) ).*H_aux;

if(representa)
    [X, Y] = meshgrid(-floor(W/2):floor(W/2));
    h = exp( (-1/2)*((X.^2+Y.^2)/sigma^2));
    figure, surf(X,Y,h)
end

end

