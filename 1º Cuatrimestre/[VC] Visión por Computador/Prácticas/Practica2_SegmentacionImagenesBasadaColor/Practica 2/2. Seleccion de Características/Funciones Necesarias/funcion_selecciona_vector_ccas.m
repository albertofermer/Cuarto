function [espacioccas,Jespacioccas] = funcion_selecciona_vector_ccas(XoI,YoI,dim)

% XoI: Matriz con todos las instancias
% YoI: Vector columna donde se señalan las instancias que pertenecen a la
% clase.
% dim: Número de componentes del espacio de características.
  
c = combnk(1:12,dim);
[nf,nc] = size(c);

Jaux = zeros(nf,1);

    for i=1:nf
        espacio = c(i,:);
        X = XoI(:,espacio);
        Jaux(i,1) = indiceJ(X,YoI);
    end  

espacioccas = c(Jaux>=0.1,:);
[Jespacioccas,indMaximo] = max(Jaux);

espacioccas = c(indMaximo,:);
    
end