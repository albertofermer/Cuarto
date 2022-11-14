function Ifiltrada = funcion_filtadapt_matricial(I,matriz_vecindad, ...
    VarRuido)
%-------------------------------------------------------------------------%
% Altera su comportamiento en función de las características locales de la
% imagen tratando de conservar los detalles de la imagen y eliminar el ruido.
% Tiene en cuenta la varianza del ruido total de la imagen (VarRuido) y la
% varianza local del ruido en la ventana de cada píxel (varLocal).
%
% De esta forma:
% 
% Si no hay ruido en la imagen (VarRuido = 0) devuelve la
% imagen sin filtrar.
%
% Si el ruido global es similar al ruido local (VarRuido ~ varLocal) el
% cociente da 1 y por tanto aplica un filtro de medias en la ventana de la
% imagen. Estas zonas son zonas donde no existen bordes.
%
% Si el ruido global es mucho menor que el ruido local
% (VarRuido<<varLocal), entonces corresponde a un borde y el cociente da 0
% y no hay filtrado.
%-------------------------------------------------------------------------%
[NH, MH] = size(matriz_vecindad);

varLocal = stdfilt(double(I),matriz_vecindad).^2;
H_promedio = (1/(NH*MH))*matriz_vecindad;

% Utilizamos el padding de symmetric porque la función stdfilt lo utiliza
% internamente.

mediaLocal = imfilter(double(I),H_promedio,'symmetric');

Ifiltrada = uint8( double(I) - double( (     ( VarRuido ./ varLocal ) )   .*    (  double(I) - double(mediaLocal)  )        )  );

% Convertimos la imagen al mismo tipo de dato que la entrada:
if(isinteger(I))
    Ifiltrada = uint8(Ifiltrada);
else
    Ifiltrada = double(Ifiltrada);
end


end

