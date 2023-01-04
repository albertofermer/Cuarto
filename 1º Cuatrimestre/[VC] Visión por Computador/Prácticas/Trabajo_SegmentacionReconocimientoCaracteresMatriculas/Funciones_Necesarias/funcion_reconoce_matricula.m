function Matricula=funcion_reconoce_matricula(rutaImagen)

addpath('../Funciones_Necesarias\')
load('../Material_Imagenes_Plantillas/00_Plantillas/Plantillas.mat')
load('../Variables_Generadas/num_pix_min_normalizado.mat')


% Las plantillas están formadas por 26 caracteres rotados cada uno en
% ángulos diferentes.
Objetos = 26;
Angulos = 7;
% Posibles Caracteres que puede tener la matrícula
Caracteres = '0123456789ABCDFGHKLNRSTXYZ';
% La matrícula final será:
Matricula = "";

% Leemos la imagen de entrada
Ic = imread(rutaImagen);

% Segmentamos la matrícula para obtener la imagen etiquetada junto con su
% contorno y el número de caracteres que hay en la imagen.
[Ietiq, numFiguras, ~, perimetro] = funcion_segmentaMatriculas(Ic,num_pix_min,false);

% Por cada caracter que haya en la imagen se hace:
for figura = 1:numFiguras
    % Obtenemos la imagen con un solo caracter
    Ietiq_figura = Ietiq == figura;
    % Inicializamos la matriz de correlación de ese caracter.
    correlacion = zeros(Objetos,Angulos);
    % Recortamos el símbolo utilizando el contorno que hemos obtenido de la
    % funcion_segmentaMatriculas.
    simbolo = Ietiq_figura(perimetro(1+(4*(figura-1) ),2):perimetro(3+(4*(figura-1) ),2),perimetro(1+(4*(figura-1) ),1):perimetro(2+(4*(figura-1) ),1));
    % Rellenamos la matriz de correlación:
    %figure('Name',"Tabla de Correlacion"),
    for indC=1:Objetos
        for indA=1:Angulos
            sentencia = "plantilla = Objeto" + num2str(indC,'%02d') + "Angulo" + num2str(indA,'%02d') + ";";
            eval(sentencia);
            [N,M] = size(plantilla);
            % Reescalamos la imagen recortada para poder utilizar la
            % función de correlación.
            simbolo_resize =  imresize(simbolo,[N,M]);
            % Obtenemos el valor de correlación entre ambas imágenes.
            correlacion(indC,indA) = funcion_correlacionEntreMatrices(simbolo_resize,plantilla);
            
            
        end
    end


    % El valor que nos interesa será la fila de la matriz de correlación
    % que tenga el valor máximo. La fila nos indica el caracter qué
    % corresponde con la figura que hemos estudiado.
    [f,c] = find(correlacion == max(correlacion(:)));

    % Añadimos el caracter f a la matricula.
    disp(figura)
    %correlacion
    eval("plantilla = Objeto" + num2str(f,'%02d') + "Angulo" + num2str(c,'%02d') + ";");
    figure('Name','Correlacion'),
    subplot(1,2,1), imshow(plantilla), title(Caracteres(f))
    subplot(1,2,2), imshow(Ietiq_figura)
    %pause;
    Matricula = Matricula + Caracteres(f);
    
end
% Ponemos como título de la imagen segmentada la matrícula.
title(Matricula)
end

