function NormCrossCorr = funcion_normcorr2(I,T)

[N,M] = size(I);
[TN,TM] = size(T);

NormCrossCorr = zeros(N,M);

for i=1+floor(TN/2):N-floor(TN/2)
    for j = 1+floor(TM/2):M-floor(TM/2)
        ROI = I(i-floor(TN/2):i+floor(TN/2),j-floor(TM/2):j+floor(TM/2));
        CCN = sum( (ROI(:)-mean(ROI(:))) .* (T(:) - mean(T(:)) ))/...
            sqrt(sum( (ROI(:)-mean(ROI(:))).^2) .* sum((T(:) - mean(T(:)) ).^2));
        NormCrossCorr(i,j) = CCN;
    end
end

end

