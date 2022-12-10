function ValorCorrelacion = funcion_correlacionEntreMatrices (Matriz1, Matriz2)

M1 = mean(Matriz1(:));
M2 = mean(Matriz2(:));

Matriz1menosMedia = Matriz1-M1;
Matriz2menosMedia = Matriz2-M2;

Matriz_numerador = Matriz1menosMedia.*Matriz2menosMedia;
numerador = sum(sum(Matriz_numerador));

Matriz1c = Matriz1menosMedia.^2;
Matriz2c = Matriz2menosMedia.^2;

denominador = sqrt( sum(sum(Matriz1c)) * sum(sum(Matriz2c)) );

ValorCorrelacion = numerador/denominador;
end