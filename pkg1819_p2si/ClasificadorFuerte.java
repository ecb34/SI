/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1819_p2si;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eduardo
 */
public class ClasificadorFuerte {
    public List<HiperPlano> hiperplanos;
    public double confiabilidad;

    public void setConfiabilidad(double confiabilidad) {
        this.confiabilidad = confiabilidad;
    }

    public int aplicarClasificador(Imagen img){
        List<Imagen> datos = new ArrayList<>();
        datos.add(img);
        double resultado = 0;
        for(HiperPlano hp : hiperplanos){
            List<Integer> res = hp.aplicarClasificadorDebil(datos);
            resultado += hp.getAlpha() * res.get(0); //solo hay una imagen
        }
        if(resultado < 0)
            resultado = -1.0;
        else
            resultado = 1.0;
        
        return (int) resultado;
    }
    
    public double getError(List<Imagen> x, List<Integer> y){
        double res = 0.0;
        int resClasificador;
        int size = x.size();
        for(int i=0; i<size;i++){
            resClasificador = aplicarClasificador(x.get(i));
            if(resClasificador != y.get(i))
                res++;
              
        }
        return res/size;
    }
    
    public List<HiperPlano> getHiperplanos() {
        return hiperplanos;
    }

    public void setHiperplanos(List<HiperPlano> hiperplanos) {
        this.hiperplanos = hiperplanos;
    }


    public double getConfiabilidad() {
        return confiabilidad;
    }

    
    public ClasificadorFuerte(){
        hiperplanos = new ArrayList<>();
    }

    @Override
    public String toString() {
      String s = confiabilidad+ ";";
      for(HiperPlano p : hiperplanos){
          s += p.toString() + "*";
      }
      return s;
    }
}
