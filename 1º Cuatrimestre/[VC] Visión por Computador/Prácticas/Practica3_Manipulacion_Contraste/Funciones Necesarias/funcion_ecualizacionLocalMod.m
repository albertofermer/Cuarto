function [Ieq_local] = funcion_ecualizacionLocalMod(Imagen)
% Versión modificada de la función ecualizacionLocal. Se calcula la
% ecualización de una imagen mediante una ventana de 5x5.

% En esta versión no hace falta padding porque se recorre la imagen
% original ecualizando los píxeles de 25 en 25.


[N, M] = size(Imagen);                  % Sacamos el tamaño de la imagen original
Ieq_local = uint8(zeros(N,M));          % Inicialización de la imagen resultado.
NumFilVent = 5;                         % Número de Filas de la ventana.
NumColVent = 5;                         % Número de columnas de la ventana.


for k=1:NumFilVent:N        % Recorremos la imagen original de 5 en 5
    for j=1:NumColVent:M
    
        % En el caso de que se supere el límite de la imagen, lo corregimos
        % y ecualizamos una imagen de tamaño inferior. Esto sucede cuando
        % las dimensiones de la imagen no es múltiplo de 5.

     if(k+NumFilVent > N && j+NumColVent > M)   % Si se supera altura y anchura.
        ROI = Imagen(k:N, j:M);
        sub_img_eq = uint8(funcion_ecualizaImagen(ROI,2));
        Ieq_local(k:N, j:M) = sub_img_eq;

     elseif(k+NumFilVent > N)                   % Si se supera solo la altura
        ROI = Imagen(k:N, j:j+NumColVent);
        sub_img_eq = uint8(funcion_ecualizaImagen(ROI,2));
        Ieq_local(k:N, j:j+NumColVent) = sub_img_eq;

     elseif(j+NumColVent > M)                   % Si se supera solo la anchura
        ROI = Imagen(k:k+NumFilVent, j:M);
        sub_img_eq = uint8(funcion_ecualizaImagen(ROI,2));
        Ieq_local(k:k+NumFilVent, j:M) = sub_img_eq;


     else % Si la ventana se encuentra dentro de los límites de la imagen
        ROI = Imagen(k:k+NumFilVent, j:j+NumColVent);
        sub_img_eq = uint8(funcion_ecualizaImagen(ROI,2));
        Ieq_local(k:k+NumFilVent, j:j+NumColVent) = sub_img_eq;
     end

    end
end

% Convertimos la imagen resultado a el mismo tipo de dato que la imagen
% original.
if(isinteger(Imagen))
    Ieq_local = uint8(Ieq_local);
else
    Ieq_local = double(Ieq_local);
end

end

