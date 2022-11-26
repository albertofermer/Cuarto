%-------------------------------------------------------------------------%
%%                      SEGUNDA  PARTE                                   %%
%               Segmentación de señales de tráfico (rojas)                %
%-------------------------------------------------------------------------%
addpath('Funciones Necesarias\')
addpath('Funciones_THCircular\')

%-------------------------------------------------------------------------%
% 1. Lee la imagen, genera y visualiza una imagen binaria (Ib) de puntos
% "rojos" de la imagen en color candidatos a pertenecer al contorno de la
% región que se pretende segmentar. Para ello utiliza un metodo de
% umbralización global.
%-------------------------------------------------------------------------%

nombre = "ImagenesPractica\SegundaParte\Signal";

signals = 3;
for s = 1:signals
    nombre_signal = nombre + num2str(s);
    numImagenes = 2;

    if (s == 1) 
        numImagenes = 4;
    end

    for imagenes=1:numImagenes

        nombre_imagen = nombre_signal + "_" + num2str(imagenes) + ".tif";
        Imagen = imread(nombre_imagen);
        
        % 1. Generar y visualizar una imagen binaria de puntos rojos de la imagen
        % de color candidatos a pertenecer al contorno de la región que se pretende
        % segmentar. Utilizar un método de umbralización global, tomando como
        % umbrales de referencia para cada componente de color:
        
        factor = 0.35;
        R = Imagen(:,:,1); G = Imagen(:,:,2); B = Imagen(:,:,3);
        
        Rmin = min(R(:)); Rmax = max(R(:));
        Gmin = min(G(:)); Gmax = max(G(:));
        Bmin = min(B(:)); Bmax = max(B(:));
        
        UmbralRojo = factor*(Rmin+Rmax);
        UmbralVerde = factor*(Gmin+Gmax);
        UmbralAzul = factor*(Bmin+Bmax);
        
        Rbin = R>UmbralRojo;
        Gbin = G<UmbralVerde;
        Bbin = B<UmbralAzul;
        
        Ib = and(Rbin,and(Gbin,Bbin)); % Multiplicamos las tres imagenes para segmentar las tres componentes.
        figure,
        imshow(Ib)
        
        % 2. Aplica a Ib la transformada de Hough para detectar contornos
        % circulares utilizando la función circle_hough.
        
        radii = 5:2:35; % Radios posibles de las circunferencias buscadas
        
        % Todas las imágenes tienen la misma resolución. Por lo tanto todos los
        % objetos circulares tienen su radio limitado a este rango.
        
        H = circle_hough(Ib,radii,'same');
        
        % La opción 'same' hace que los centros de la circunferencia sean centros
        % pertenecientes a píxeles de la imagen.
        
        % Interpretación de las dimensiones de H:
        %
        % H tiene esas dimensiones (360x480x16) porque 360x480 son los píxeles de
        % la imagen y, por tanto, cada píxel se convierte en un posible centro de
        % cada círculo. La tercera dimensión (16) corresponde con cada uno de los
        % radios que le hemos pasado a la función.
        % 
        
        % Calcula manualmente H(180,90,6) e interpreta el resultado.
        H(180,90,6); % = 0
        % En el píxel 180,90 no hay una circunferencia de radio 6.
        
        % Ayudándote de la funcion find escribe la ecuacion de la circunferencia
        % que pasa por más puntos en Ib. Especificando su radio y coordenadas del
        % centro. 
        
        matCirc = [];
        for r = 1:length(radii)
            Hr = H(:,:,r);
            [fil,col] = find(H(:,:,r) == max(Hr(:)));
            matCirc = [matCirc; r, radii(r), fil(1), col(1), H(fil(1),col(1),r)];
        end
        % La circunferencia que contiene más puntos será:
        [~,pos] = max(matCirc(:,5));
        circ_mas_probable = matCirc(pos,:);
        
        % Escribir la ecuación de la circunferencia:
        disp(num2str(circ_mas_probable(2))+"^2 = (x - " + num2str(circ_mas_probable(3)) + ")^2 + ( y - " + num2str(circ_mas_probable(4)) + ")^2")
        disp(" La circunferencia contiene " + num2str(circ_mas_probable(end)) + " puntos.")
        
        % Encuentra los parámetros representativos de la circunferencia más votada
        % aplicando la funcion circle_houghpeaks con la siguiente configuracion:
        P = circle_houghpeaks(H,radii,'npeaks',1);
        
        % El parámetro P contiene el radio, las coordenadas del centro y el
        % número de puntos por los que pasa la circunferencia.
        
        
        
        % 11. A partir de la información contenida en P, genera una imagen binaria
        % de las mismas dimensiones que las matrices que componen la imagen
        % original que especifique los píxeles correspondientes a la circunferencia
        % detectada. Para ello, usa la función circlepoints.
        
        [N,M] = size(R);
        Ib_circunf = zeros(N,M);
        
        [x,y] = circlepoints(P(3));
        for i = 1:length(x)
            Ib_circunf(y(i)+P(2),x(i)+P(1)) = 1;
        end
        figure,
        imshow(Ib_circunf)
        
        % 12. Genera y visualiza la imagen binaria que representa la segmentación
        % de la señal de tráfico Ib_circle utilizando la funcion imfill:
        
        Ib_circulo = imfill(Ib_circunf,'holes');
        
        figure, imshow(Imagen .* uint8(Ib_circulo)) 
        pause;
        close all
    end
end