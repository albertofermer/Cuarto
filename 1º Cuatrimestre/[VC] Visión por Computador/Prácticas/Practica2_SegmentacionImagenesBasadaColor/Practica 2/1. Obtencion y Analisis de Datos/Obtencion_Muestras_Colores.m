% En esta parte se obtendrá como salida los colores representativos de la
% imagen en los diferentes modelos de color: RGB, HSI, YUV y Lab.

% PRIMERO: Añadimos al path la carpeta donde tenemos las imágenes que vamos
% a utilizar.
clear
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
    R_i = I_color(:,:,1);
    G_i = I_color(:,:,2);
    Bl_i = I_color(:,:,3);
    
        % Normalización:

        R_i = double(R_i) / 255;
        G_i = double(G_i) / 255;
        Bl_i = double(Bl_i) / 255;

    I_gris = uint8(mean(I_color,3));
    % MODELO HSI:

    I_hsv = rgb2hsv(I_color);
    H_i = I_hsv(:,:,1);
    S_i = I_hsv(:,:,2);

    % El nivel de iluminación se calcula como la media de intensidad de
    % los canales R, G y B.

    I_i = uint8(mean(I_color,3));

        % Normalización:

        I_i = double(I_i)/255;

    % MODELO YUV:
    
        % Las componentes deben implementarse a partir de las
        % transformaciones facilitadas en el apartado 2.2 del Tema 2.

     Y = R_i * 0.299; 
     U = 0.492 * (Bl_i - Y); 
     V = 0.877 * (R_i - Y);

    % imshow(cat(3,Y,U,V));

    % MODELO Lab:

    I_lab = rgb2lab(I_color);
    L_i = I_hsv(:,:,1);
    A_i = I_hsv(:,:,2);
    B_i = I_hsv(:,:,3);

        % Normalización:

    % Guardo las matrices de la imagen i.

    RGB = [R_i,G_i,Bl_i];
    HSI = [H_i,S_i,I_i];
    YUV = [Y, U, V];
    LAB = [L_i,A_i,B_i];

    % nombre_fichero = ['./Variables_Generadas/Datos_Color_',num2str(i)];
    % save(nombre_fichero,"RGB","HSI","LAB","YUV");


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

     nombre_fichero = ['./Variables_Generadas/ValoresColores'];
     save(nombre_fichero,"ValoresColores","CodifValoresColores");

     rmpath('../../Material_Imagenes\01_MuestrasColores\');
     clear
