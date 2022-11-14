function ISNR = funcion_evalua_ruido(Ioriginal,Iruidosa,Ifiltrada)
%-------------------------------------------------------------------------%
% Para evaluar el ruido de una imagen utilizaremos la relación señal-ruido
% (ISNR).
%
% De esta forma el numerador será el error cuadrático de la imagen antes de
% filtrar y el denominador el error cuadrático de la imagen después de
% filtrar.
%
% Para que consideremos que la imagen ha sido filtrada correctamente el
% denominador deberá de ser menor que el numerador dando como resultado un
% número positivo (después de aplicarle el logaritmo)
%
% Por el contrario, si el resultado es negativo, consideramos que el ruido
% de la iamgen filtrada es superior al de la imagen sin filtrar y, por
% tanto, el filtro no ha funcionado correctamente.
%-------------------------------------------------------------------------%

num = (  (double(Ioriginal) - double(Iruidosa)).^2 );
denom = (  (double(Ioriginal) - double(Ifiltrada)).^2 );

ISNR = 10*log10(sum(num(:))/sum(denom(:)));


end

