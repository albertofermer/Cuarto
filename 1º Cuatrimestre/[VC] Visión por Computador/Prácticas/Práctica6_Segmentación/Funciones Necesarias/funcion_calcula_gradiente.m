function [Gx,Gy,modG] = funcion_calcula_gradiente(Img,Hx,Hy)

if (isinteger(Img))
    Img = double(Img);
end

Gx = imfilter(Img,Hx,'replicate');
Gy = imfilter(Img,Hy,'replicate');
modG = sqrt(Gx.^2 + Gy.^2);


end

