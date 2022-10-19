%% Obtención de las muestras de color

% En esta parte se obtendrá como salida los colores representativos de la
% imagen en los diferentes modelos de color: RGB, HSI, YUV y Lab.

% PRIMERO: Añadimos al path la carpeta donde tenemos las imágenes que vamos
% a utilizar.
addpath('../../Material_Imagenes\01_MuestrasColores\');


% SEGUNDO: Comenzamos el tratamiento de las imágenes. Para ello haremos un
% procesamiento en cadena aprovechando que los nombres de los ficheros
% siguen un patron.

numImagenes = 3;
ValoresCodif = [255, 128, 64, 32];

ValoresColores = [];
CodifValoresColores = [];
for i = 1:numImagenes

     nombre = ['Color', num2str(i)];
    I_seg = imread(['../../Material_Imagenes/01_MuestrasColores/', nombre, '_MuestraColores.tif']);
    I_color = imread([nombre, '.jpeg']);
    
    % Para cada imagen a color, tendremos que sacar las matrices
    % R,G,B,H,S,I,Y,U,V,L,a y b

    % MODELO RGB:
    R_i = double(I_color(:,:,1))/255;
    G_i = double(I_color(:,:,2))/255;
    Bl_i = double(I_color(:,:,3))/255;

    I_gris = uint8(mean(I_color,3));
    % MODELO HSI:

    I_hsv = rgb2hsv(I_color);
    H_i = I_hsv(:,:,1);
    S_i = I_hsv(:,:,2);

    % El nivel de iluminación se calcula como la media de intensidad de
    % los canales R, G y B.

    I_i = uint8(mean(I_color,3)); % (R + G + B) / 3

        % Normalización:

        I_i = double(I_i)/255;

    % MODELO YUV:
    
        % Las componentes deben implementarse a partir de las
        % transformaciones facilitadas en el apartado 2.2 del Tema 2.

     Y = double(R_i) * 0.299 + 0.587*double(G_i) + 0.114*double(Bl_i); 
     U = 0.492 * (double(Bl_i) - Y); 
     V = 0.877 * (double(R_i) - Y);

     U = mat2gray(U,[-0.6,0.6]);
     V = mat2gray(V,[-0.6,0.6]);

    % imshow(cat(3,Y,U,V));

    % MODELO Lab:

    I_lab = rgb2lab(I_color);
    L_i = I_lab(:,:,1);
    A_i = I_lab(:,:,2);
    B_i = I_lab(:,:,3);

        % Normalización:
        L_i = L_i/100;
        A_i = mat2gray(A_i,[-128,127]);
        B_i = mat2gray(B_i,[-128,127]);

    % Guardo las matrices de la imagen i.

    RGB = [R_i,G_i,Bl_i];
    HSI = [H_i,S_i,I_i];
    YUV = [Y, U, V];
    LAB = [L_i,A_i,B_i];


    % 3. Genera la matriz ValoresColores, compuesta por los valores 
    % normalizados de  R G B,H S I,Y U V,L a b en los píxeles etiquetados 
    % de todas las imágenes marcadas manualmente.

    for j = ValoresCodif

        % Concatenamos los valores que hay en la imagen segmentada que
        % tengan el valor de cada elemento del vector de valores
        % codificados.
        
        ValoresColores = [ValoresColores;   R_i(I_seg == j), G_i(I_seg == j), Bl_i(I_seg == j), ...
            H_i(I_seg == j), S_i(I_seg == j), I_i(I_seg == j), ...
            Y(I_seg == j), U(I_seg == j), V(I_seg == j),...
            L_i(I_seg == j),A_i(I_seg == j), B_i(I_seg == j)];

        % Se debe generar el vector CodifValoresColores que especifica la
        % codificación del color al que corresonden las filas de las matrices
        % de datos anteriores.

        % Para ello utilizaremos un vector de 1's del tamaño de los píxeles
        % que haya para cada valor del vector de valores.
        
        CodifValoresColores = [CodifValoresColores; ones(length(R_i(I_seg == j)),1)*j];        

    end
end

    % TERCERO: Por último, podemos guardar los resultados de los colores y
    % las codificaciones de cada píxel de las imagenes segmentadas.

    % nombre_fichero = ['./Variables_Generadas/ValoresColores'];
    % save(nombre_fichero,"ValoresColores","CodifValoresColores");

     rmpath('../../Material_Imagenes\01_MuestrasColores\');


 %% Representación de las muestras de color.

     % Representación de las muestras de color obtenidas en los diferentes
% espacios de color considerados.


% PRIMERO: Cargamos los datos generados en el paso anterior:
    % load('Variables_Generadas/ValoresColores.mat');

% Representación Datos RGB: Los píxeles color Rojo-Fresa (255) se representan en
% Rojo, los de color Verde-Fresa (128) en verde, los de color Verde-Planta (64) en
% azul y, por último, los píxeles color Negro-Lona (32) en negro.

    R = ValoresColores(:,1);
    G = ValoresColores(:,2);
    B = ValoresColores(:,3);

    valoresCodif = unique(CodifValoresColores);
    valoresPlot = [".black",".b",".g",".r"];

    for i = 1:length(valoresCodif)
        plot3(R(CodifValoresColores == valoresCodif(i)), ...
            G(CodifValoresColores == valoresCodif(i)), ...
            B(CodifValoresColores == valoresCodif(i)), ...
            valoresPlot(i)), hold on
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
        plot(H(CodifValoresColores == valoresCodif(i)),...
            S(CodifValoresColores == valoresCodif(i)), ...
            valoresPlot(i)), hold on
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
        plot(U(CodifValoresColores == valoresCodif(i)),...
            V(CodifValoresColores == valoresCodif(i)),...
            valoresPlot(i)), hold on
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
        plot(a(CodifValoresColores == valoresCodif(i)),...
            b(CodifValoresColores == valoresCodif(i)),...
            valoresPlot(i)), hold on
    end

    grid on
    title('Representación a-b')
    xlabel('a')
    ylabel('b')
    legend(["Negro-Lona","Verde-Planta","Verde-Fresa","Rojo-Fresa"])


% Recalculamos el valor de H para que no esté dividido...

    
    Hrecalculado = H;
    Hrecalculado(H<=.5) = 1 - 2*H(H<=.5);
    Hrecalculado(H>.5) = 2*(H(H>.5) - .5);


    figure()
    for i = 1:length(valoresCodif)

        plot(Hrecalculado(CodifValoresColores == valoresCodif(i)), ...
            S(CodifValoresColores == valoresCodif(i)), ...
            valoresPlot(i)), hold on
    end

    grid on
    title('(fixed) Representación H-S')
    xlabel('Hue')
    ylabel('Saturation')
    legend(["Negro-Lona","Verde-Planta","Verde-Fresa","Rojo-Fresa"])

    % Guardamos el valor H recalculado.
    
    ValoresColores(:,4) = Hrecalculado;

     nombre_fichero = './Variables_Generadas/ValoresColores';
     save(nombre_fichero,"ValoresColores","CodifValoresColores");

    %clear
