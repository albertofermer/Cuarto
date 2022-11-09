function Ifiltrada = funcion_filtadapt_matricial(I,matriz_vecindad, ...
    VarRuido,opcion_padding)

% opcion = 'zeros', 'replicate', 'symmetric'
[N, M] = size(I);
[NH, MH] = size(matriz_vecindad);
EF = floor(NH/2);
EC = floor(MH/2);
Ifiltrada = zeros(size(I));

if(~strcmp('zeros',opcion_padding) && ~strcmp('replicate',opcion_padding) && ~strcmp('symmetric',opcion_padding))
    disp('Opcion no válida');
    return;
end

if(strcmp('zeros',opcion_padding))
    % Amplía la imagen con ceros
    Iamp = zeros(2*EF+N,2*EC+M);
    Iamp = uint8(Iamp);
    % Incrustamos la imagen en la imagen ampliada.
    Iamp(1+EF:(N+EF), 1+EC:(M+EC)) = I;
    

elseif (strcmp('replicate',opcion_padding))
    Iamp = padarray(I,[EF,EC],'replicate');

elseif (strcmp('symmetric',opcion_padding))
    Iamp = padarray(I,[EF,EC],'symmetric');
    
end

varLocal = stdfilt(double(I),matriz_vecindad).^2;
H_promedio = (1/(NH*MH))*matriz_vecindad;
mediaLocal = funcion_imfilter(double(I),H_promedio,opcion_padding);

Ifiltrada = uint8( double(I) - double( (     ( VarRuido ./ varLocal ) )   .*    (  double(I) - double(mediaLocal)  )        )  );

% Convertimos la imagen al mismo tipo de dato que la entrada:
if(isinteger(I))
    Ifiltrada = uint8(Ifiltrada);
else
    Ifiltrada = double(Ifiltrada);
end


end

