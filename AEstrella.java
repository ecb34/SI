/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AEstrella;

import java.util.ArrayList;


/**
 *
 * @author mirse
 */
public class AEstrella {
 
    //Mundo sobre el que se debe calcular A*
    Mundo mundo;
    
    //Camino
    public char camino[][];
    
    //Casillas expandidas
    int camino_expandido[][];
    
    //Número de nodos expandidos
    int expandidos;
    
    //Coste del camino
    float coste_total;
    
    public AEstrella(){
        expandidos = 0;
        mundo = new Mundo();
    }
    
    public AEstrella(Mundo m){
        //Copia el mundo que le llega por parámetro
        mundo = new Mundo(m);
        camino = new char[m.tamanyo_y][m.tamanyo_x];
        camino_expandido = new int[m.tamanyo_y][m.tamanyo_x];
        expandidos = 0;
        
        //Inicializa las variables camino y camino_expandidos donde el A* debe incluir el resultado
            for(int i=0;i<m.tamanyo_x;i++)
                for(int j=0;j<m.tamanyo_y;j++){
                    camino[j][i] = '.';
                    camino_expandido[j][i] = -1;
                }
    }
    
    //Calcula el A*
    public int CalcularAEstrella(){
        boolean encontrado = false;
        int result = -1;
        coste_total = -1f;
        
        ArrayList<Nodo> listaInterior = new ArrayList<Nodo>();
        ArrayList<Nodo> listaFrontera = new ArrayList<Nodo>();
        Nodo inicial = new Nodo(mundo.getCaballero());// g = 0
        inicial.setH(calcularH(inicial.getPosicion()));
        inicial.setF();
        listaFrontera.add(inicial);
        while(!listaFrontera.isEmpty()){
            Nodo n = menorF(listaFrontera);//tomar nodo con menor f, en primera iteracion será el inicial
            if(mundo.getCelda(n.getX(), n.getY())== 'd'){
                encontrado = true;
                reconstruirCamino(n);  
                coste_total = n.getG();
                result = n.getG();
                listaFrontera.clear();
            }
            else{ 
                listaFrontera.remove(n);
                listaInterior.add(n);
                agregarExpandido(n);
                expandidos++;
                calcularVecinos(n,listaInterior,listaFrontera);
            } 
        }
        //Si ha encontrado la solución, es decir, el camino, muestra las matrices camino y camino_expandidos y el número de nodos expandidos
        if(encontrado){
            //Mostrar las soluciones
            System.out.println("Camino");
            mostrarCamino();
            System.out.println("Camino explorado");
            mostrarCaminoExpandido();         
            System.out.println("Nodos expandidos: "+expandidos);
        }
     
        return result;
    }
    

    public void calcularVecino(Nodo n, Coordenada vecino, int valorVecino, ArrayList<Nodo> listaInterior, ArrayList<Nodo> listaFrontera) {
        Boolean enInterior = false;
        for (Nodo nInterior : listaInterior) {                        
            if (nInterior.getX() == vecino.getX() && nInterior.getY() == vecino.getY())
                enInterior = true;       
        }
        if (!enInterior) {
            int gInterior = n.getG() + valorVecino;
            Boolean enFrontera = false;
            for (Nodo nFrontera : listaFrontera) {
                if (nFrontera.getX() == vecino.getX() && nFrontera.getY() == vecino.getY()) {
                    enFrontera = true;
                    if (nFrontera.getG() > gInterior) {
                        nFrontera.setPadre(n);
                        nFrontera.setG(gInterior);
                        nFrontera.setF();
                    }
                }
            }
            if (!enFrontera) {
                float h = calcularH(vecino);
                Nodo m = new Nodo(vecino, h + gInterior, gInterior,h, n);
                listaFrontera.add(m);
            }
        }
    }
    public void calcularVecinos(Nodo n, ArrayList<Nodo> listaInterior, ArrayList<Nodo> listaFrontera){
        int movimientos_pares[][] = new int[6][2];
        int movimientos_impares[][] = new int[6][2];
        
        //Establece los movimientos posibles para las filas pares
        movimientos_pares[0][0] = -1; movimientos_pares[0][1] = 0; //Hacia arriba izquierda, resta una fila
        movimientos_pares[1][0] = -1; movimientos_pares[1][1] = 1; //Hacia arriba derecha, resta una fila y suma una columna
        movimientos_pares[2][0] = 0; movimientos_pares[2][1] = -1; //Retroceder izquierda, resta columna
        movimientos_pares[3][0] = 0; movimientos_pares[3][1] = 1; //Avanzar derecha, suma columna
        movimientos_pares[4][0] = 1; movimientos_pares[4][1] = 0; //Hacia abajo izquierda, suma fila
        movimientos_pares[5][0] = 1; movimientos_pares[5][1] = 1; //Hacia abajo derecha, suma fila y suma columna

        //Establece los movimientos posibles para las filas impares
        movimientos_impares[0][0] = -1; movimientos_impares[0][1] = -1; //Hacia arriba izquierda, resta una fila y resta una columna
        movimientos_impares[1][0] = -1; movimientos_impares[1][1] = 0; //Hacia arriba derecha, resta una fila
        movimientos_impares[2][0] = 0; movimientos_impares[2][1] = -1; //Retroceder izquierda, resta columna
        movimientos_impares[3][0] = 0; movimientos_impares[3][1] = 1; //Avanzar derecha, suma columna
        movimientos_impares[4][0] = 1; movimientos_impares[4][1] = -1; //Hacia abajo izquierda, suma fila y resta una columna
        movimientos_impares[5][0] = 1; movimientos_impares[5][1] = 0; //Hacia abajo derecha, suma fila
        
        int x_vecino;
        int y_vecino;
        Coordenada vecino;
        int valorVecino;
        for(int i=0; i<6; i++){
            if(n.getY()%2 == 0){
                y_vecino = n.getY() + movimientos_pares[i][0];
                x_vecino = n.getX() + movimientos_pares[i][1];                
            }else{
                 y_vecino = n.getY() + movimientos_impares[i][0];
                 x_vecino = n.getX() + movimientos_impares[i][1];
            }
            char celda = mundo.getCelda(x_vecino, y_vecino);
            if(celda!='p' && celda!='b'){//diferente valor si aparece dragon (?)
                valorVecino = calculaValor(celda);// se pasa la casilla para calcular su valor
                vecino = new Coordenada(x_vecino,y_vecino); //vecino = m
                calcularVecino(n, vecino, valorVecino, listaInterior, listaFrontera);
            }
        }
    }
    public Nodo menorF(ArrayList<Nodo> lista){
        float menorF=  Integer.MAX_VALUE;
        Nodo menorNodo = null;
        for(Nodo nodo: lista){//modificar para que seleccione alguno cuando hay dos menores
           if(nodo.getF() < menorF){
               menorF = nodo.getF();
               menorNodo = nodo;
           }
        }
        return menorNodo;
    }
    public int calculaValor(char casilla){
        if(casilla == 'c'  || casilla=='d')
            return 1;
        if(casilla =='h')
            return 2;
        if(casilla =='a')
            return 3;
        
        return 0;       
    }
    public float[] evenr_to_cube(Coordenada c){
        float x = c.getX() - (c.getY() + (c.getY()&1)) /2;
        float z = c.getY();
        float y = -x-z;     //x * z  
        float[] res = {x,y,z};
        return res;
    }
    public float manhattanHexagonal(Coordenada n, Coordenada dragon){
        float[] cuboN = evenr_to_cube(n);
        float[] cuboD = evenr_to_cube(dragon);
        float x = Math.abs(cuboD[0]- cuboN[0]);
        float y = Math.abs(cuboD[1]- cuboN[1]);
        float z = Math.abs(cuboD[2]- cuboN[2]);
        return (x+y+z)/2;
    }
    public float euclideaHexagonal(Coordenada n, Coordenada dragon){
        float[] cuboN = evenr_to_cube(n);
        float[] cuboD = evenr_to_cube(dragon);
        float x = cuboD[0] - cuboN[0];
        float y =  cuboD[1] - cuboN[1];
        float z =  cuboD[2] - cuboN[2];
        x = (float) Math.pow(x,2);
        y = (float) Math.pow(y,2);
        z = (float) Math.pow(z,2);
        return (float) Math.sqrt((x+y+z)/2);
    }
    public float euclidea3d(Coordenada n, Coordenada dragon){
        float[] cuboN = evenr_to_cube(n);
        float[] cuboD = evenr_to_cube(dragon);
        float x = cuboD[0] - cuboN[0];
        float y =  cuboD[1] - cuboN[1];
        float z =  cuboD[2] - cuboN[2];
        x = (float) Math.pow(x,2);
        y = (float) Math.pow(y,2);
        z = (float) Math.pow(z,2);
        return (float) Math.sqrt(x+y+z);
    }
    public float manhattan(Coordenada n, Coordenada dragon){
        float x = dragon.getX() - n.getX();
        float y = dragon.getY() - n.getY();
        float absX = Math.abs(x);
        float absY = Math.abs(y);
        return absX + absY;
    }
    public float euclidea(Coordenada n, Coordenada dragon){
            float x = dragon.getX() - n.getX();
            float y = dragon.getY() - n.getY();
            x = (float) Math.pow(x,2);
            y = (float) Math.pow(y,2);
            return (float) Math.sqrt(x+y);
    }
    public float calcularH(Coordenada n){
        Coordenada dragon = mundo.getDragon();
        float res = manhattanHexagonal(n,dragon); 
        return res;
    }
    
    public void reconstruirCamino(Nodo dragon){
        
       int x = dragon.getX();
       int y = dragon.getY();
       camino[y][x] = 'X';
       Nodo padre = dragon.getPadre();
       while(padre!=null){//verificar
           x = padre.getX();
           y = padre.getY();
           camino[y][x]='X';
           padre = padre.getPadre();
       }
    }
    public void agregarExpandido(Nodo n){
        camino_expandido[n.getY()][n.getX()] = expandidos;
    }
    
    //Muestra la matriz que contendrá el camino después de calcular A*
    public void mostrarCamino(){
        for (int i=0; i<mundo.tamanyo_y; i++){
            if(i%2==0)
                System.out.print(" ");
            for(int j=0;j<mundo.tamanyo_x; j++){
                System.out.print(camino[i][j]+" ");
            }
            System.out.println();   
        }
    }
    
    //Muestra la matriz que contendrá el orden de los nodos expandidos después de calcular A*
    public void mostrarCaminoExpandido(){
        for (int i=0; i<mundo.tamanyo_y; i++){
            if(i%2==0)
                    System.out.print(" ");
            for(int j=0;j<mundo.tamanyo_x; j++){
                if(camino_expandido[i][j]>-1 && camino_expandido[i][j]<10)
                    System.out.print(" ");
                System.out.print(camino_expandido[i][j]+" ");
            }
            System.out.println();   
        }
    }
    
    public void reiniciarAEstrella(Mundo m){
        //Copia el mundo que le llega por parámetro
        mundo = new Mundo(m);
        camino = new char[m.tamanyo_y][m.tamanyo_x];
        camino_expandido = new int[m.tamanyo_y][m.tamanyo_x];
        expandidos = 0;
        
        //Inicializa las variables camino y camino_expandidos donde el A* debe incluir el resultado
            for(int i=0;i<m.tamanyo_x;i++)
                for(int j=0;j<m.tamanyo_y;j++){
                    camino[j][i] = '.';
                    camino_expandido[j][i] = -1;
                }
    }
    
    public float getCosteTotal(){
        return coste_total;
    }
}

class Nodo{
    private float f,h;
    private int g;
    private Coordenada posicion;
    private Nodo padre;
    
    public Nodo(Coordenada p){
        posicion = new Coordenada(p);
        g=0;
        f=h= 0f;
        padre = null;
    }
    public Nodo(Nodo p){
        posicion = new Coordenada(p.getPosicion());
        this.f = p.f;
        this.g = p.g;
        this.h = p.h;
        padre = p.getPadre();    
    }
    
    public Nodo(Coordenada c, float f, int g, float h, Nodo p){
        posicion = c;
        this.f = f;
        this.g = g;
        this.h = h;
        this.padre = p;
    }

    public float getF() {
        return f;
    }

    public Nodo getPadre() {
        return padre;
    }

    public void setPadre(Nodo padre) {
        this.padre = padre;
    }

    public void setF() {
        this.f = g + h;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public float getH() {
        return h;
    }

    public void setH(float h) {
        this.h = h;
    }

    public Coordenada getPosicion() {
        return posicion;
    }

    public void setPosicion(Coordenada posicion) {
        this.posicion = posicion;
    }
    public int getX(){
        return this.posicion.getX();
    }
    public int getY(){
        return this.posicion.getY();
    }
}