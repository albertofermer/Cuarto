function Ifilt = funcion_umbralizacion_local(I,H,opcion)

%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%

[N, M] = size(I);
[NH, MH] = size(H);

% Vecindad impar:
if(mod(NH,2)==0)
NH = NH+1;
end
if(mod(MH,2)==0)
MH = MH+1;
end

EF = floor(NH/2);
EC = floor(MH/2);



if(~strcmp('zeros',opcion) && ~strcmp('replicate',opcion) && ~strcmp('symmetric',opcion))
    disp('Opcion no válida');
    return;
end

if(strcmp('zeros',opcion))
    % Amplía la imagen con ceros
    Iamp = zeros(NH+N,MH+M);
    Iamp = uint8(Iamp);
    % Incrustamos la imagen en la imagen ampliada.
    Iamp(1+EC:(N+EC), 1+EF:(M+EF)) = I;

else
    Iamp = padarray(I, [EF, EC], opcion);    
end

%-------------------------------------------------------------------------%
%-------------------------------------------------------------------------%

for k=(1+EF):(N+EF)
    for j=(1+EC):(M+EC)
        ROI = Iamp(k-EF:k+EF, j-EC:j+EC);
        media = mean(ROI(:));
        Ifilt(k-EF, j-EC) = ROI(EF+1,EC+1)<media-2;
    end
end

Ifilt = logical(Ifilt);

end

