
    addpath('./Funciones Necesarias/')
% Problema de Clasificación binaria de píxeles.
%   Clase '0' :- Píxel de otro color.
%   Clase '1' :- Píxel de color rojo-fresa.

% Cargamos los datos de la etapa anterior.
    load('./Variables Necesarias/ValoresColores.mat');

    CoI = ValoresColores(CodifValoresColores == 255,:);
    YoI = CodifValoresColores == 255;
    
    valores_agrupaciones = 3:6;
    separabilidad_i = zeros(size(valores_agrupaciones));
    espacios_ccas = {
        [0 0 0];
        [0 0 0 0];
        [0 0 0 0 0];
        [0 0 0 0 0 0]};

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

    comb3descr = ValoresColores(:,cell2mat(espacios_ccas(1)));
    [~,pos] = max(separabilidad_i(2:end)); pos = pos + 1;
    combinacion_mas_de_tres_descriptores = cell2mat(espacios_ccas(pos));

     save('./Variables Generadas/espacioccasYseparabilidad',"espacios_ccas","separabilidad_i");
     save('./Variables Generadas/variablesGeneradas',"RGB","Lab","comb3descr","combinacion_mas_de_tres_descriptores");