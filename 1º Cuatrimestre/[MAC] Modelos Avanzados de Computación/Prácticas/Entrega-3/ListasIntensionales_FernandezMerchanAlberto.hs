
ejercicio1 = [x+10 | x <- [1..10]]
ejercicio2 =  [ [x] | x <- [1..10], even x ]
ejercicio3 = [ [11-x] | x <- [1..10] ]
ejercicio4 = [odd x | x <- [1..10]]
ejercicio5 = [(3*x, x<=3) | x <- [1..10], x<=6]
ejercicio6 = [(5*x, x==10) | x <- [1..10], x<=3 || x==8]
ejercicio7 = [(x+10,x+11) | x <- [1..10], odd x]
ejercicio8 = [ [5..x+6] | x<-[1..10], x<=7, odd x]
ejercicio9 = [5*(11-x)-4 | x <- [1..10], x>5]
ejercicio10 = [ [(x+2),x..4] | x<-[1..10], even x]

