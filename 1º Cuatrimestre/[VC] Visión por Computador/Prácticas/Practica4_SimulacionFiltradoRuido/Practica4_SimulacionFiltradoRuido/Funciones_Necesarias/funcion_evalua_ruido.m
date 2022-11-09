function ISNR = funcion_evalua_ruido(Ioriginal,Iruidosa,Ifiltrada)


num = (  (double(Ioriginal) - double(Iruidosa)).^2 );
denom = (  (double(Ioriginal) - double(Ifiltrada)).^2 );

ISNR = 10*log10(sum(num(:))/sum(denom(:)));


end

