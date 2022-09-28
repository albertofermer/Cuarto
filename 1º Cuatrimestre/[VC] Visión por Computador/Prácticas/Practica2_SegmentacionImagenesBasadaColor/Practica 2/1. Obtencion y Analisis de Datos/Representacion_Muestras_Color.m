
% Representación de las muestras de color obtenidas en los diferentes
% espacios de color considerados.


% PRIMERO: Cargamos los datos generados en el paso anterior:
    load('Variables_Generadas/ValoresColores.mat');

% Representación Datos RGB: Los píxeles color Rojo-Fresa (255) se representan en
% Rojo, los de color Verde-Fresa (128) en verde, los de color Verde-Planta (64) en
% azul y, por último, los píxeles color Negro-Lona (32) en negro.

    R = ValoresColores(:,1);
    G = ValoresColores(:,2);
    B = ValoresColores(:,3);

    valoresCodif = unique(CodifValoresColores);
    valoresPlot = [".black",".b",".g",".r"];

    for i = 1:length(valoresCodif)
        plot3(R(CodifValoresColores == valoresCodif(i)),G(CodifValoresColores == valoresCodif(i)),B(CodifValoresColores == valoresCodif(i)),valoresPlot(i)), hold on
    end
    grid on
    title('Representación RGB')
    xlabel('Componente Roja')
    ylabel('Componente Verde')
    zlabel('Componente Azul')
    legend(["Negro-Lona","Verde-Planta","Verde-Fresa","Rojo-Fresa"])
    hold off
% Representación de los valores H y S de los píxeles de color Rojo-Fresa (255) en rojo,
% los de color Verde-Fresa (128) en verde, los Verde-Planta (64) en azul y,
% por último, los Negro-Lona(32) en Negro.

    figure()
    H  = ValoresColores(:,4);
    S = ValoresColores(:,5);

    for i = 1:length(valoresCodif)
        plot(H(CodifValoresColores == valoresCodif(i)),S(CodifValoresColores == valoresCodif(i)),valoresPlot(i)), hold on
    end

    grid on
    title('Representación H-S')
    xlabel('H')
    ylabel('S')
    legend(["Negro-Lona","Verde-Planta","Verde-Fresa","Rojo-Fresa"])


% Representación de los valores U - V de los píxeles de color Rojo-Fresa (255) en rojo,
% los de color Verde-Fresa (128) en verde, los Verde-Planta (64) en azul y,
% por último, los Negro-Lona(32) en Negro.

    figure()
    U  = ValoresColores(:,8);
    V = ValoresColores(:,9);

    for i = 1:length(valoresCodif)
        plot(U(CodifValoresColores == valoresCodif(i)),V(CodifValoresColores == valoresCodif(i)),valoresPlot(i)), hold on
    end

    grid on
    title('Representación U-V')
    xlabel('U')
    ylabel('V')
    legend(["Negro-Lona","Verde-Planta","Verde-Fresa","Rojo-Fresa"])



% Representación de los valores a - b de los píxeles de color Rojo-Fresa (255) en rojo,
% los de color Verde-Fresa (128) en verde, los Verde-Planta (64) en azul y,
% por último, los Negro-Lona(32) en Negro.

    figure()
    a  = ValoresColores(:,11);
    b = ValoresColores(:,12);

    for i = 1:length(valoresCodif)
        plot(a(CodifValoresColores == valoresCodif(i)),b(CodifValoresColores == valoresCodif(i)),valoresPlot(i)), hold on
    end

    grid on
    title('Representación a-b')
    xlabel('a')
    ylabel('b')
    legend(["Negro-Lona","Verde-Planta","Verde-Fresa","Rojo-Fresa"])


% Recalculamos el valor de H para que no esté dividido...

    
    Hrecalculado = H;
    Hrecalculado(H<=.5) = 1 - 2*H(H<=0.5);
    Hrecalculado(H>.5) = 2*(H(H>0.5) - 0.5);


    figure()
    for i = 1:length(valoresCodif)
        plot(Hrecalculado(CodifValoresColores == valoresCodif(i)),S(CodifValoresColores == valoresCodif(i)),valoresPlot(i)), hold on
    end

    grid on
    title('(fixed) Representación H-S')
    xlabel('H')
    ylabel('S')
    legend(["Negro-Lona","Verde-Planta","Verde-Fresa","Rojo-Fresa"])
