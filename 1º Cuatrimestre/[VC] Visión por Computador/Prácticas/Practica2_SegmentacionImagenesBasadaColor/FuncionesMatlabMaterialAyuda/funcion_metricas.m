function [Sens Esp Prec FalsosPositivos] = funcion_metricas(Aut, Gold)

Aut = Aut>0;
Gold = Gold>0;

% CALCULO DE SENS

NumPixPositive = sum((double(Gold(:))));

TruePositive = double(Aut).*double(Gold);
NumPixTruePositive = sum(TruePositive(:));

Sens = NumPixTruePositive/NumPixPositive;

% CALCULO DE ESP
GoldN = not(Gold);
NumPixNegative = sum((double(GoldN(:))));

TrueNegative = double(not(Aut)).*double(GoldN);
NumPixTrueNegative = sum(TrueNegative(:));

Esp = NumPixTrueNegative/NumPixNegative;

% CALCULO DE PREC

[N M]=size(Gold);

NumPix = N*M;

Prec = (NumPixTruePositive+NumPixTrueNegative)/NumPix;

% FALSOS POSITIVOS
FalsePositive = double(Aut).*double(GoldN);
NumPixFalsePositive = sum(FalsePositive(:));

FalsosPositivos = NumPixFalsePositive / NumPixNegative;


