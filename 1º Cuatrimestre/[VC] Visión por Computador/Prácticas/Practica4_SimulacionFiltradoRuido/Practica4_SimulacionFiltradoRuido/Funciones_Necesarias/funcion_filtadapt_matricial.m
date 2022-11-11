function Ifiltrada = funcion_filtadapt_matricial(I,matriz_vecindad, ...
    VarRuido)

[NH, MH] = size(matriz_vecindad);

varLocal = stdfilt(double(I),matriz_vecindad).^2;
H_promedio = (1/(NH*MH))*matriz_vecindad;
mediaLocal = imfilter(double(I),H_promedio,'symmetric');

Ifiltrada = uint8( double(I) - double( (     ( VarRuido ./ varLocal ) )   .*    (  double(I) - double(mediaLocal)  )        )  );

% Convertimos la imagen al mismo tipo de dato que la entrada:
if(isinteger(I))
    Ifiltrada = uint8(Ifiltrada);
else
    Ifiltrada = double(Ifiltrada);
end


end

