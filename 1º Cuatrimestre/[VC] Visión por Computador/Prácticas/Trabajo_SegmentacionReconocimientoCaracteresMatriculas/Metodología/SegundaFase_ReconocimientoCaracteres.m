%% Segunda Fase: Reconocimiento de Caracteres %%
clear all; close all; clc



addpath('../Funciones_Necesarias\')
load('../Material_Imagenes_Plantillas/00_Plantillas/Plantillas.mat')
load('../Variables_Generadas/num_pix_min_normalizado.mat')
ruta = "../Material_Imagenes_Plantillas/01_Training/";
imagen = 1;
Angulos = 7;
Objetos = 26;
Caracteres = '0123456789ABCDFGHKLNRSTXYZ';
Cadena = "";

nombre = ruta + "Training_" + num2str(imagen,'%02d') + ".jpg";
Ic = imread(nombre);
[Ietiq, numFiguras, centroides, perimetro] = funcion_segmentaMatriculas(Ic,num_pix_min,false);

for figura = 1:numFiguras
    Ietiq_figura = Ietiq == figura;
    correlacion = zeros(Objetos,Angulos);
    simbolo = Ietiq_figura(perimetro(1+(4*(figura-1) ),2):perimetro(3+(4*(figura-1) ),2),perimetro(1+(4*(figura-1) ),1):perimetro(2+(4*(figura-1) ),1));
    for indC=1:Objetos
        for indA=1:Angulos
            sentencia = "plantilla = Objeto" + num2str(indC,'%02d') + "Angulo" + num2str(indA,'%02d') + ";";
            eval(sentencia);
            [N,M] = size(plantilla);
            simbolo_resize =  imresize(simbolo,[N,M]);
            correlacion(indC,indA) = funcion_correlacionEntreMatrices(simbolo_resize,plantilla);
        end
    end

    [f,~] = find(correlacion == max(correlacion(:)));
    Cadena = Cadena + Caracteres(f);

end
title(Cadena)



