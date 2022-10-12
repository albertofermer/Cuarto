%% MAHALANOBIS
%
% Permite realizar una aproximación de "esfera elíptica"
% Se adoptarán los siguientes criterios
    % máxima distancia de mahalanobis.

    % excluir 3% de los ptos rojo fresa mas alejado del centroide de la
    % nube de puntos

    % distancia mahalanobis minima de la nube de puntos de la calse de
    % fondo.

    %

    % PROCEDIMIENTO PARA DISEÑAR UN CLASIFICADOR
        % 1. Eliminar valores anómalos en la clase de interés 
        % (componente roja < 0.95).
        % 2. Diseño de clasificadores en carpetas SEPARADAS.
        %   Es necesario escoger un umbral en base a la tasa de acierto.
        % necesitamos: centroide, mcov y umbral

        % Tasa de acierto: Numero de pixeles que ha acertado respecto a la
        % imagen Gold. Se tienen en cuenta ambas clases.

        % Sensibilidad: Cuantos pixeles ha acertado en la clase positiva
        
        % Especificidad: Aciertos en la clase negativa 

        % (funcion_metricas)


        % 3. Reducir la resolución de las imágenes a la mitad

        % 4. Una vez se hayan aplicado las técnicas, hay que reescalar los
        % resultados (nearest).
% =========================================================================

% Guardar un .mat por cada modelo.

% [CentrosRadios,MatrizCov] = funcion_Mahalanobis(DatosRGB_Rojo,DatosRGB_NoRojo)









