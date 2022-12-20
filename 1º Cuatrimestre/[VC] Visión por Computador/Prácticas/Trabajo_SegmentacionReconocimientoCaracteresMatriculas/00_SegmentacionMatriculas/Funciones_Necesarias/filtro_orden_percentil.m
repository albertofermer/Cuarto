function ImagenNueva = filtro_orden_percentil(I,H,per,opcion)

[N, M] = size(I);
ImagenNueva = zeros(size(I));
% EF y EC son el número de filas y de columnas, respectivamente, que se
% añadirán con la opción de padding.

[NH,MH] = size(H);

EF = floor(NH/2);
EC = floor(MH/2);

if(~strcmp('zeros',opcion) && ~strcmp('replicate',opcion) && ~strcmp('symmetric',opcion))
    disp('Opcion no válida');
    return;
end

if(strcmp('zeros',opcion))
    % Amplía la imagen con ceros
    Iamp = zeros(NH+N,MH+M);
    Iamp = uint8(Iamp);
    % Incrustamos la imagen en la imagen ampliada.
    Iamp(1+EC:(N+EC), 1+EF:(M+EF)) = I;

else
    Iamp = padarray(I, [EF, EC], opcion);    
end

%-------------------------------------------------------------------------%
% Para aplicar la máscara H a la imagen se recorrerá la imagen ampliada y
% se hará la operación de convolución para cada píxel.
%
% ROI: entorno de vecindad del píxel.
% conv: matriz multiplicada por la máscara.
% ImagenNueva: La imagen con el píxel actualizado.
%-------------------------------------------------------------------------%

for k=(1+EF):(N+EF)
    for j=(1+EC):(M+EC)
    ROI = Iamp(k-EF:k+EF, j-EC:j+EC);
    ImagenNueva(k-EF, j-EC) = prctile(ROI(:),per);
    end
end

end