
% Para eliminar posibles objetos ruidosos escogeremos como umbral el número
% de píxeles que forma el número '1'.

I = imread("Material_Imagenes_Plantillas\01_Training\Training_01.jpg");
[N,M] = size(I);
ROI = roipoly(I);
num_pix_min = sum(ROI(:))/(N*M);

save('num_pix_min_normalizado',"num_pix_min")