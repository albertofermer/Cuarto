function H = funcion_calcula_mascara_gaussiana_eficiente(W,sigma,representa)

[X, Y] = meshgrid(-floor(W/2):floor(W/2));
H_aux = exp( (-1/2)*((X.^2+Y.^2)/sigma^2));
H = ( 1/(sum(H_aux(:))) ).*H_aux;

if(representa)
    [X, Y] = meshgrid(-floor(W/2):floor(W/2));
    h = exp( (-1/2)*((X.^2+Y.^2)/sigma^2));
    figure, surf(X,Y,h)
end

end

