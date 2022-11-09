function Ifiltrada = funcion_filtadapt(I,matriz_vecindad, ...
    VarRuido,opcion_padding)

% opcion = 'zeros', 'replicate', 'symmetric'
[N, M] = size(I);
[NH, MH] = size(matriz_vecindad);
EF = floor(NH/2);
EC = floor(MH/2);

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
    Ifiltrada = zeros(size(I));

elseif (strcmp('replicate',opcion_padding))
    Iamp = padarray(I,[EF,EC],'replicate');

elseif (strcmp('symmetric',opcion_padding))
    Iamp = padarray(I,[EF,EC],'symmetric');
    
end

for k=(1+EF):(N+EF)
    for j=(1+EC):(M+EC)
        entorno_vecindad = Iamp(k-EF:k+EF, j-EC:j+EC);
        varLocal = var(double(entorno_vecindad(:))); % Calcula la varianza del entorno de vecindad (Varianza Local)
        mediaLocal = mean(double(entorno_vecindad(:)));      % Promedia el entorno de vecindad (Media)
        Ifiltrada(k-EF, j-EC) = uint8( double(I(k-EF, j-EC)) - ( (VarRuido/varLocal)*(  double(I(k-EF, j-EC)) - mediaLocal ) ));
    end
end

% Convertimos la imagen al mismo tipo de dato que la entrada:
if(isinteger(I))
    Ifiltrada = uint8(Ifiltrada);
else
    Ifiltrada = double(Ifiltrada);
end


end

