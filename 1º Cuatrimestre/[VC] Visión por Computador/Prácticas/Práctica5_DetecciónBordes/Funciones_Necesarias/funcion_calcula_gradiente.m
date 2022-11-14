function [Gx,Gy,modG] = funcion_calcula_gradiente(I,Hx,Hy)

if (isinteger(I))
    Img = double(I);
end

Gx = imfilter(Img,Hx,'replicate');
Gy = imfilter(Img,Hy,'replicate');
modG = sqrt(Gx.^2 + Gy.^2);


end

