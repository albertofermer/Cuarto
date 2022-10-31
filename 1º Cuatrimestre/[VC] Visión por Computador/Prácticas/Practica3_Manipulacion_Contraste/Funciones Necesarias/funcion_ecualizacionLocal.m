function [Ieq_local] = funcion_ecualizacionLocal(Imagen,NumFilVent,NumColVent, OpcionRelleno)
%contador = 0
if(mod(NumFilVent,2) == 0)
    NumFilVent = NumFilVent + 1;
end

if(mod(NumColVent,2) == 0)
    NumColVent = NumColVent + 1;
end

[N, M] = size(Imagen);
EF = floor(NumFilVent/2);
EC = floor(NumColVent/2);

if(~strcmp('zeros',OpcionRelleno) && ~strcmp('replicate',OpcionRelleno) && ~strcmp('symmetric',OpcionRelleno))
    disp('Opcion no válida');
    return;
end

if(strcmp('zeros',OpcionRelleno))
    % Amplía la imagen con ceros
    Iamp = zeros(2*EF+N,2*EC+M);
    Iamp = uint8(Iamp);
    % Incrustamos la imagen en la imagen ampliada.
    Iamp(1+EF:(N+EF), 1+EC:(M+EC)) = Imagen;
    Ieq_local = zeros(size(Imagen));

elseif (strcmp('replicate',OpcionRelleno))
    Iamp = padarray(Imagen,[EF,EC],'replicate');

elseif (strcmp('symmetric',OpcionRelleno))
    Iamp = padarray(Imagen,[EF,EC],'symmetric');
    
end

for k=(1+EF):(N+EF)
    for j=(1+EC):(M+EC)

        ROI = Iamp(k-EF:k+EF, j-EC:j+EC);
%         ROI_aux = ROI;
%         ROI_aux(round(NumFilVent/2),round(NumColVent/2)) = 255;
%         imshow(uint8(ROI_aux));
        sub_img_eq = uint8(funcion_ecualizaImagen(ROI,2));
        Ieq_local(k-EF, j-EC) = sub_img_eq(round(NumFilVent/2),round(NumColVent/2));
%        contador = contador + 1

    end
end

if(isinteger(Imagen))
    Ieq_local = uint8(Ieq_local);
else
    Ieq_local = double(Ieq_local);
end

end

