% -----------------------------------------------------------------------%
%%                            Segunda Fase
%                   Reconocimiento de Caracteres
% -----------------------------------------------------------------------%

clear all; close all; clc
addpath('../Funciones_Necesarias/')

% ------------------------------------------------------------------------%
% Si la variable training = true utilizará las imágenes de entrenamiento,
% si training = false, usará las imágenes de test.
%
% Si la variable mostrar = true mostrará cada una de las imágenes
% intermedias que se han generado en la función. Si mostrar = false, solo
% mostrará la imagen de entrada y la imagen segmentada.
% ------------------------------------------------------------------------%

training = true;

if (training)
    ruta = "../Material_Imagenes_Plantillas/01_Training/Training_";
    numImagenes = 5;
else
    ruta = "../Material_Imagenes_Plantillas/02_Test/Test_";
    numImagenes = 20;
end

% ------------------------------------------------------------------------%
% Cargamos el umbral obtenido para aplicarselo al bwareaopen dentro de la
% función. En caso de que, tras segmentar la imagen queden agrupaciones
% ruidosas en la línea central de la misma con una cantidad de píxeles
% menor a la del número '1', las eliminará.
% ------------------------------------------------------------------------%

load('../Variables_Generadas/num_pix_min_normalizado.mat')

for imagen = 1:numImagenes

    nombre = ruta + num2str(imagen,'%02d') + ".jpg"

    funcion_reconoce_matricula(nombre);

    pause; close all;

end

% ------------------------------------------------------------------------%
% En la imagen de test numero 8 hay un defecto debido a que al segmentar la
% imagen, el logo de la UE se divide en dos y, como el algoritmo solo
% descarta el primer objeto de la imagen, mantiene el segundo fragmento.
% ------------------------------------------------------------------------%

rmpath('../Funciones_Necesarias/')
