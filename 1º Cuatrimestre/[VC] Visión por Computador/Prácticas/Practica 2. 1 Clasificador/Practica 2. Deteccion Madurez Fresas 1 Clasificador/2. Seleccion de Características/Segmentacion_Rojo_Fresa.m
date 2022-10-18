
    addpath('./Funciones Necesarias/')
% Problema de Clasificación binaria de píxeles.
%   Clase '0' :- Píxel de otro color.
%   Clase '1' :- Píxel de color rojo-fresa.

% Cargamos los datos de la etapa anterior.
    load('./Variables Necesarias/ValoresColores.mat');

    % Los valores de interés son aquellos píxeles que representan el color rojo-fresa
    YoI = CodifValoresColores == 255; 
    
    % Vamos a utilizar agrupaciones de 3, 4, 5 y 6 características.

    valores_agrupaciones = 3:6;
    
    % El vector que nos resultará tendrá 4 posiciones (el tamaño de
    % valores_agrupaciones)

    separabilidad_i = zeros(size(valores_agrupaciones));
    
    % Como vamos a almacenar diferentes vectores de características con
    % distintos tamaños, podemos usar un array de celdas.

    espacios_ccas = {
        [0 0 0];
        [0 0 0 0];
        [0 0 0 0 0];
        [0 0 0 0 0 0]};

    % Iteramos sobre el vector de valores_agrupaciones para calcular el
    % mayor grado de separabilidad y qué características lo aportan para 
    % cada combinación de las mismas.

     for i = 1:length(valores_agrupaciones)
     
         [espacioccas, separabilidad_i(i)] = funcion_selecciona_vector_ccas(ValoresColores, ...
            YoI,valores_agrupaciones(i));

        espacios_ccas(i) = {espacioccas};
     
     end


    % Nos quedaremos con los siguientes descriptores:
        % RGB
        % Lab
        % Mejor combinación de 3 descriptores.
        % Combinación más adecuada de más de 3 descriptores.

    RGB = ValoresColores(:,1:3);
    Lab = ValoresColores(:,10:12);

    RSL = ValoresColores(:,cell2mat(espacios_ccas(1))); % RSL
    indices3descr = cell2mat(espacios_ccas(1));
    [~,pos] = max(separabilidad_i(2:end)); pos = pos + 1;
    %descriptores = cell2mat(espacios_ccas(pos));
    HSIUab = ValoresColores(:,cell2mat(espacios_ccas(pos)));
    %HSIUab
     save('./Variables Generadas/espacioccasYseparabilidad',"espacios_ccas","separabilidad_i");
     save('./Variables Generadas/variablesGeneradas',"RGB","Lab","RSL","HSIUab");

     % Representacion RSL. La mejor de 3 descriptores.

        valor1 = ValoresColores(:,indices3descr(1));
        valor2 = ValoresColores(:,indices3descr(2));
        valor3 = ValoresColores(:,indices3descr(3));

    plot3(valor1(CodifValoresColores == 255), ...
        valor2(CodifValoresColores == 255), ...
        valor3(CodifValoresColores == 255),'.r'), hold on


    plot3(valor1(CodifValoresColores ~= 255), ...
        valor2(CodifValoresColores ~= 255), ...
        valor3(CodifValoresColores ~= 255),'.g')

rmpath('./Funciones Necesarias/')