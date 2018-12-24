/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1819_p2si;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author alu
 */
public class Adaboost {
    
    public int n_imgs,a,t; //num imgs,pruebas y clasificadores maximos.
    
    public Adaboost( int a, int t){
        this.a = a;
        this.t = t;
    }
    
    public ClasificadorFuerte algoritmo(List<Imagen> x, List<Integer> y, List<Imagen> test, List<Integer> yTest){
        int dimensiones = x.get(0).getImageData().length;
        n_imgs = x.size();
        List<Double> d = new ArrayList();//distribucion inicialmente uniforme
        for(int i=0;i<n_imgs;i++)
            d.add(1.0/n_imgs); //dificultad cada punto de las imagenes: inicalmente todas igual 
        
        ClasificadorFuerte fuerte = new ClasificadorFuerte();
        for(int j=0;j<t;j++){
            HiperPlano mejor = new HiperPlano(Double.MAX_VALUE);
            for(int k=0; k<a;k++){
                HiperPlano hp = generarClasificadorAzar(dimensiones);
                double error_hp = hp.obtenerErrorClasificador(x,y,d);
                if(error_hp >= 0.5)//si el error que comete es mas del 50%, no lo agregamos y generamos otro
                    k--;
                else if(error_hp < mejor.getError())
                    mejor = new HiperPlano(hp);    
            }     
            mejor.setAlpha(0.5* (Math.log((1- mejor.getError())/ mejor.getError())));
            fuerte.getHiperplanos().add(mejor);  // el que tenga menor error lo agregamos al clasificador fuerte
            List<Integer> res = mejor.aplicarClasificadorDebil(x);
            
            double error = fuerte.getError(x,y);//entrenamiento
            //fuerte.setConfiabilidad(0.5*(Math.log((1-error)/error)));
            
            double errortest = fuerte.getError(test, yTest); // sacar el error para el conjunto de imagenes de test
            fuerte.setConfiabilidad(0.5*(Math.log((1-errortest)/errortest)));
            System.out.println("t=" + j+ " error=" + error + " errortest=" + errortest);
            if(error==0 || errortest ==0)
                break;
            double zt = 0.0;
               
            for(int i=0; i<n_imgs;i++){//actualizamos el valor de D
               double valorD = d.get(i) * Math.pow(Math.E, -mejor.getAlpha() * y.get(i) * res.get(i));
               zt += d.get(i);
               d.set(i,valorD);
            }
            for(int i=0; i<n_imgs;i++){
                d.set(i,d.get(i)/zt);
            }         
        }
        return fuerte;
    }
    
    public HiperPlano generarClasificadorAzar(int d){
        double sumatorio = 0.0;
        List<Integer> punto = new ArrayList();
        HiperPlano clasificador = new HiperPlano();
        SplittableRandom rng = new SplittableRandom();
        double c = 0.0;
        for(int i=0; i<d;i++){
            punto.add(rng.nextInt(-128,128));
            double random= rng.nextDouble(-1,1);
            clasificador.getVectorNormal().add(random);
            //c+= random * rng.nextDouble(0,256); //sumatorio de vectornormal * punto en cada pos
            sumatorio +=random;
        }
        //clasificador.setC(c);
        for(int i=0; i<d;i++){
            clasificador.getVectorNormal().set(i, clasificador.getVectorNormal().get(i)/sumatorio);
            c+= punto.get(i)* clasificador.getVectorNormal().get(i);
        }
        clasificador.setC(c);
        return clasificador;
    }
    
    

    
}
