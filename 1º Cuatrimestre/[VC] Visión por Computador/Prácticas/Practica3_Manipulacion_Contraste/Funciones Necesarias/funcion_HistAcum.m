function [HistAcum] = funcion_HistAcum(h)
% Función que calcula el histograma acumulado de una imagen a partir de su
% histograma (h)
HistAcum = zeros(256,1);
HistAcum(1) = h(1);
for i=2:256
    HistAcum(i) = HistAcum(i-1) + h(i);
end

