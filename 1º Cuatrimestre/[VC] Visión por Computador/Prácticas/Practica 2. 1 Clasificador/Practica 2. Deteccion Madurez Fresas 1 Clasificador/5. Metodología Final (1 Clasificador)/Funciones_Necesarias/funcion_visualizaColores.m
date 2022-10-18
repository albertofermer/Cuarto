function [Io] = funcion_visualizaColores(ImagenColor, ImagenBinaria, flagRepresenta)
%%  FUNCION_VISUALIZA
% ========================================================================
% ENTRADA:
% 
% - Ii: Imagen de entrada en color.
%
% - Ib: Imagen binaria que indica qué pixeles hay que cambiar de color.
%           255 - Rojo
%
% - flagRepresenta: Es una variable booleana que indica si se debe mostrar
%                   la figura (True) o no (False).
% 
% SALIDA:
% 
% - Io: Imagen de salida que representa la imagen Ii con los píxeles 
%       indicados en Ib cambiados al color indicado en Color.
% 
        
Io = ImagenColor;
R = uint8(Io(:,:,1)); G = uint8(Io(:,:,2)); B = uint8(Io(:,:,3));
Colores = [255,128,64,32];

R(ImagenBinaria(:,:) == 255) = 255;
G(ImagenBinaria(:,:) == 255) = 0; 
B(ImagenBinaria(:,:) == 255) = 0;


R(ImagenBinaria(:,:) == 128) = 255;
G(ImagenBinaria(:,:) == 128) = 255; 
B(ImagenBinaria(:,:) == 128) = 0;
        
Io = cat(3,R,G,B); 

if(flagRepresenta)                                               
    figure, imshow(Io);
end

end