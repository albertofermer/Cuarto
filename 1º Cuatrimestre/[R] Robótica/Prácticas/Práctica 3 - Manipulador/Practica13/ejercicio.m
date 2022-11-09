%% GUIA 3
% Ejercicio1
matriz_pinza=eye(4,4); % matriz unidad
posicion=[40 -10 0];
alfa=0; beta=0; gamma=0; % Angulos de Euler

matriz_pieza=Desplazamiento(posicion(1), posicion(2), posicion(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionx(gamma);

%pinta_pieza_delgada(matriz_pieza)
pinta_bloque(matriz_pieza,'b')
matriz_agarre = Desplazamiento(0,0,4)*Rotacionz(-pi/2)*Rotacionx(pi);
matriz_pinza_global = matriz_pieza*matriz_agarre;

%q=[0 -1.5700 -1.5700 -1.5700 1.5700 0];

codo = -1;
avance = 1;
simetrico = 0;

[q1, q2, q3, q4, q5, q6]=inv_kinema_ur3_new(matriz_pinza_global,codo,avance,simetrico);
figure,
matriz=funcion_pinta_UR3_new([q1 q2 q3 q4 q5 q6], matriz_pinza_global);

deg = rad2deg([q1, q2, q3, q4, q5, q6])
%% Ejercicio 2
matriz_pinza=eye(4,4); % matriz unidad
posicion=[20 -10 0];
alfa=0; beta=0; gamma=0; % Angulos de Euler
  
matriz_pieza=Desplazamiento(posicion(1), posicion(2), posicion(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionx(gamma);

%pinta_pieza_delgada(matriz_pieza)

pinta_bloque(matriz_pieza,'b')
matriz_agarre = Desplazamiento(0,0,4)*Rotacionz(pi)*Rotacionx(pi);
matriz_pinza_global = matriz_pieza*matriz_agarre;

codo = 1;
avance = 1;
simetrico = 0;

[q1, q2, q3, q4, q5, q6]=inv_kinema_ur3_new(matriz_pinza_global,codo,avance,simetrico);

matriz=funcion_pinta_UR3_new([q1 q2 q3 q4 q5 q6], matriz_pinza_global);

% Ejercicio 3

matriz_pinza=eye(4,4); % matriz unidad
posicion=[20 -10 0];
alfa=0; beta=0; gamma=0; % Angulos de Euler
  
matriz_pieza=Desplazamiento(posicion(1), posicion(2), posicion(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionx(gamma);


pinta_bloque(matriz_pieza,'b')
matriz_agarre = Desplazamiento(0,-0.8,4)*Rotaciony(pi/2)*Rotacionx(-pi/2);
matriz_pinza_global = matriz_pieza*matriz_agarre;

codo = 1;
avance = 1;
simetrico = 0;

[q1, q2, q3, q4, q5, q6]=inv_kinema_ur3_new(matriz_pinza_global,codo,avance,simetrico);

matriz=funcion_pinta_UR3_new([q1 q2 q3 q4 q5 q6], matriz_pinza_global);