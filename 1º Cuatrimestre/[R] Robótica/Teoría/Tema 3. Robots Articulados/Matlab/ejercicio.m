%   1. Desplazar al punto (5,5,5)
%   2. RotarY(pi/4)
%   3. RotarZ(-pi/4)
%   4. Desplaza respecto al Y global 5 unidades.

% Altura de bloque: 6cm ; Anchura de bloque: 3cm
% Pintar punto en (1.5, 1.5, 6) del sistema de 
% referencia local

pinta_bloque(Desplazamiento(0,0,0),'k');

%% 1.
pinta_bloque(Desplazamiento(5,5,5),'r')

%% 2. 
pinta_bloque(Desplazamiento(5,5,5)*Rotaciony(pi/4),'y')

%% 3.
pinta_bloque(Desplazamiento(5,5,5)*Rotaciony(pi/4)*Rotacionz(-pi/4),'g')

%% 4.
cla
pinta_bloque(Desplazamiento(0,5,0)*Desplazamiento(5,5,5)*Rotaciony(pi/4)*Rotacionz(-pi/4),'b')

%% Convertir coordenadas del punto (1.5, 1.5, 6) al global

punto = [1.5 1.5 6 1]';
Mt = Desplazamiento(0,5,0)*Desplazamiento(5,5,5)*Rotaciony(pi/4)*Rotacionz(-pi/4);
punto_global = Mt*punto;
plot3(punto_global(1),punto_global(2),punto_global(3),'*r')

%% EJERCICIO GUÍA



matriz_pinza=eye(4,4); % matriz unidad
posicion=[20 -10 0];
alfa=0; beta=0; gamma=0; % Angulos de Euler
  
matriz_pieza=Desplazamiento(posicion(1), posicion(2), posicion(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionx(gamma);

%pinta_pieza_delgada(matriz_pieza)
pinta_bloque(matriz_pieza,'b')
matriz_agarre = Desplazamiento(0,0,4)*Rotacionz(-pi/2)*Rotacionx(pi);
matriz_pinza_global = matriz_pieza*matriz_agarre;


q=[0 -1.5700 -1.5700 -1.5700 1.5700 0];
matriz=funcion_pinta_UR3_new(q, matriz_pinza_global);






