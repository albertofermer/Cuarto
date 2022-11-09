function H = funcion_calcula_mascara_gaussiana(W,sigma,representa)

H_aux = zeros(W);

for i=1:W
    for j=1:W
        x = -floor(W/2)+(i-1);
        y = -floor(W/2)+(j-1);
        H_aux(i,j) = exp( (-1/2)*((x^2+y^2)/sigma^2));
    end
end

H = ( 1/(sum(H_aux(:))) ).*H_aux;

if(representa)
    [X, Y] = meshgrid(-floor(W/2):floor(W/2));
    h = exp( (-1/2)*((X.^2+Y.^2)/sigma^2));
    figure, surf(X,Y,h)
end

end

