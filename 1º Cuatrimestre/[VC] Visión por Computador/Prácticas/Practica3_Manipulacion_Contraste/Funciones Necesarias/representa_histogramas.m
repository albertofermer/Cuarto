function representa_histogramas(Imagen,I_b)

IR = uint8(Imagen(:,:,1));
IG = uint8(Imagen(:,:,2));
IB = uint8(Imagen(:,:,3));

h_r = imhist(IR(I_b));
h_g = imhist(IG(I_b));
h_b = imhist(IB(I_b));

for j=1:256
plot([j, j],[0, h_r(j)],'-or','MarkerFaceColor','r','MarkerSize',4), hold on
plot([j, j],[0, h_g(j)],'-og','MarkerFaceColor','g','MarkerSize',4)
plot([j, j],[0, h_b(j)],'-ob','MarkerFaceColor','b','MarkerSize',4)
end

legend('Histograma Componente Roja','Histograma Componente Verde','Histograma Componente Azul')

end

