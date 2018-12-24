
package pkg1819_p2si;

import java.util.*;

/**
 *
 * @author alu
 */
public class HiperPlano {
    
    private double c;
    private List<Double> vectorNormal;
    private double error;
    private double alpha;

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
    
    public HiperPlano(){
        this.error = 0.0;
        vectorNormal = new ArrayList<>();
    }
    public HiperPlano(double error){
        this.error = error;
        vectorNormal = new ArrayList<>();
    }
    public HiperPlano(HiperPlano p){
        this.error = p.error;
        this.vectorNormal = p.vectorNormal;
        this.alpha = p.alpha;
        this.c = p.c;
    }

    public double getError() {
        return error;
    }
    
    //comparando los datos reales con los esperados, obtenemos el error del clasificador
    public double obtenerErrorClasificador(List<Imagen> datos, List<Integer> y, List<Double> d){
       double err = 0.0;
       List<Integer> clasificaciones = aplicarClasificadorDebil(datos);
       int sizeY = y.size();
       for(int i=0; i<sizeY;i++){
           if( y.get(i) != clasificaciones.get(i)) //si el resultado esperado es igual al real
               err += d.get(i);
       }
       this.error = err;
       return err; 
    }

    @Override
    public String toString() {// c|alpha|normal[0]|normal[1]|...normal[n]
        String s = c + "|" +alpha + "|";
        int sizevector = vectorNormal.size();
       for(int i=0; i<sizevector;i++){
           if(i<sizevector-1)
               s += vectorNormal.get(i) + "|";
           else
               s += vectorNormal.get(i);
        }
        return s;
   }
    
    //aplica el clasificador debil a los puntos 
    public List<Integer> aplicarClasificadorDebil(List<Imagen> datos){
        
        List<Integer> resultado = new ArrayList<>();
        for (Imagen dato : datos) {
            byte[] imagen = dato.getImageData();
            double aux = 0.0;
            int vectorsize = vectorNormal.size();
            for(int j=0; j<vectorsize;j++)
                aux += this.vectorNormal.get(j) * (imagen[j]);
            
            aux -= this.c; //verificar
           
            if(aux < 0) // si es 0?
                resultado.add(-1);
            else
                resultado.add(1); 
        }
        return resultado;
    } 

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public List<Double> getVectorNormal() {
        return vectorNormal;
    }

    public void setVectorNormal(List<Double> vectorNormal) {
        this.vectorNormal = vectorNormal;
    }
    

}
