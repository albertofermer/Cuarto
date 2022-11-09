function ImagenNueva = funcion_imfilter2(Imagen,H,opcion)

% opcion = 'zeros', 'replicate', 'symmetric'
[N, M] = size(Imagen);
[NH, MH] = size(H);
EF = floor(NH/2);
EC = floor(MH/2);

if(~strcmp('zeros',opcion) && ~strcmp('replicate',opcion) && ~strcmp('symmetric',opcion))
    disp('Opcion no válida');
    return;
end

if(strcmp('zeros',opcion))
    % Amplía la imagen con ceros
    Iamp = zeros(2*EF+N,2*EC+M);
    Iamp = uint8(Iamp);
    % Incrustamos la imagen en la imagen ampliada.
    Iamp(1+EF:(N+EF), 1+EC:(M+EC)) = Imagen;
    ImagenNueva = zeros(size(Imagen));

elseif (strcmp('replicate',opcion))
    Iamp = padarray(Imagen,[EF,EC],'replicate');

elseif (strcmp('symmetric',opcion))
    Iamp = padarray(Imagen,[EF,EC],'symmetric');
    
end

for k=(1+EF):(N+EF)
    for j=(1+EC):(M+EC)
    ROI = Iamp(k-EF:k+EF, j-EC:j+EC);
    conv = double(ROI) .* double(H);
    ImagenNueva(k-EF, j-EC) = uint8(sum(conv(:)));
    end
end

% Convertimos la imagen al mismo tipo de dato que la entrada:
if(isinteger(Imagen))
    ImagenNueva = uint8(ImagenNueva);
else
    ImagenNueva = double(ImagenNueva);
end

end

