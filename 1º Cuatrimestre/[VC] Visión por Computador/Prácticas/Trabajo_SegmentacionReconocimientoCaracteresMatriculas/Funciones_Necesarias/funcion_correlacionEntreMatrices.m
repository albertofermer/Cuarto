function ValorCorrelacion = funcion_correlacionEntreMatrices (I, T)

ValorCorrelacion = sum( (I(:)-mean(I(:))) .* (T(:) - mean(T(:)) ))/...
    sqrt(sum( (I(:)-mean(I(:))).^2) .* sum((T(:) - mean(T(:)) ).^2));
end