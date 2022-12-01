% -----------------------------------------------------------------------%
%%                            Primera Fase
%                   Segmentación de Caracteres 
% -----------------------------------------------------------------------%

ruta = "./Material_Imagenes_Plantillas/01_Training/";
numImagenes = 5;

W = 11; sigma = round(W/5);
for imagen=1:numImagenes
nombre = ruta + "Training_" + num2str(imagen,'%02d') + ".jpg";
I = imread(nombre);
R = I(:,:,1);

% Suavizamos la imagen con un filtro gaussiano.
Rsuavizada = imfilter(R,fspecial('gaussian',W,sigma));

% Umbralizacion Local para corregir defectos de iluminacion



imshow(Rsuavizada < 255*graythresh(Rsuavizada))




end