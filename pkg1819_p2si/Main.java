/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1819_p2si;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 *
 * @author fidel
 */
public class Main {

    static int t = 170; //num clasificadore debiles a usar
    static int a = 300; //num pruebas aleatorias
    static double parte_entrenamiento = 0.6;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        switch(args[0]){
            case "-train": //entrenar para todos los tipos de imagenes
                DBLoader ml = new DBLoader();
                ml.loadDBFromPath("./db");
                List<ClasificadorFuerte> clasificadores = new ArrayList<>(); // los 8 clasificadores fuertes
                for(int i=0; i<8; i++){
                   List<Imagen> test = new ArrayList<>();
                   List<Imagen> entrenamiento = new ArrayList<>();
                   List<Imagen> total = ml.getImageDatabaseForDigit(i);
                   double aux = (double) total.size() * parte_entrenamiento;
                   int num_entrenamiento = (int) Math.round(aux);
                   List<Integer> y = new ArrayList<>(); // resultado esperado entrenamiento
                   List<Integer> yTest = new ArrayList<>();
                   for(int j=0; j<total.size(); j++){ //cambiarlo pa escoger aleatoriamente
                       if(j<num_entrenamiento)
                       {
                           entrenamiento.add(total.get(j));
                           y.add(1);
                       }
                       else{
                            test.add(total.get(j));      
                            yTest.add(1);
                       }      
                   }
                   List<Imagen> total_otros = new ArrayList<>();
                   for(int j=0; j<8;j++){//coger de otros tipos
                       if(i!=j)// añado las otras categorias para escoger aleatoriamente
                           total_otros.addAll(ml.getImageDatabaseForDigit(j));
                   }
                   Random rng = new Random();
                   List<Imagen> entrenamiento_otros = new ArrayList<>();
                   for(int j=0; j<num_entrenamiento; j++){// añado aleatoriamente imagenes de otras categorias, en la misma cantidad q la categoria a probar
                       int num_random = rng.nextInt(total_otros.size());
                       entrenamiento_otros.add(total_otros.get(num_random));
                       total_otros.remove(num_random); // lo quito para no cogerlo de nuevo
                       y.add(-1);
                   }
                   List<Imagen> test_otros = new ArrayList<>();
                   for(int j=0; j<test.size();j++){//coger otros para test
                       int num_random = rng.nextInt(total_otros.size());
                       test_otros.add(total_otros.get(num_random));
                       total_otros.remove(num_random);
                       yTest.add(-1);
                   }
                  
                   entrenamiento.addAll(entrenamiento_otros);
                   test.addAll(test_otros);
                   System.out.println("entrenando categoria: " +i+ " num imgs entrenamiento= " + entrenamiento.size() + " num imgs test= "+test.size());
                   Adaboost adaboost = new Adaboost(a,t);
                   clasificadores.add(adaboost.algoritmo(entrenamiento, y,test,yTest));
                   
                }
                //guardar clasificadores en el fichero
                try{
                    PrintWriter pw = new PrintWriter(new FileWriter(args[1]));
                    for(ClasificadorFuerte f : clasificadores){
                        pw.println(f.toString());
                    }
                    pw.close();
                }catch(Exception e){
                    
                }
            break;
            case "-run":
                List<ClasificadorFuerte> fuertes = new ArrayList<>();
                try{
                    BufferedReader br = new BufferedReader(new FileReader(args[1]));
                    Imagen img = new Imagen(); 
                    //img.loadFromPath(args[2]); //cambiar
                    DBLoader mc = new DBLoader();
                    mc.loadDBFromPath("./db");
                     ArrayList d0imgs = mc.getImageDatabaseForDigit(3);
                     img = (Imagen) d0imgs.get(5);
                    List<Double> listRes = new ArrayList<>();
                    for(int i=0; i<8; i++){//recorro cada clasificador fuerte
                        cargarDatos(br, fuertes,i);
                        double res= fuertes.get(i).aplicarClasificador(img);
                        listRes.add(res);
                    }
                    double auxConfianza = Double.MIN_VALUE;
                    int categoria=-1;
                    for(int i=0; i<listRes.size();i++){
                        if(listRes.get(i)==1.0 && auxConfianza < fuertes.get(i).getConfiabilidad()){
                            categoria=i;
                            auxConfianza = fuertes.get(i).getConfiabilidad();
                        }
                    }
                    String s = "La imagen pertenece a "+ categoria;
                    switch(categoria){
                        case 0: s+= " :abrigos";
                        break;
                        case 1: s+= " :bolsos";
                        break;
                        case 2: s+= " :camisetas";
                        break;
                        case 3: s+= " :pantalones";
                        break;
                        case 4: s+= " :sueters";
                        break;
                        case 5: s+= " :vestidos";
                        break;
                        case 6: s+=" zapatillas";
                        break;
                        case 7: s+=" zapatos";
                        break; 
                    }
                    System.out.println(s);
                    MostrarImagen imgShow = new MostrarImagen();
                    imgShow.setImage(img);
                    imgShow.mostrar();
                    //0=abrigos 1=bolsos 2=camisetas 3=pantalones 4=sueters 5=vestidos 6=zapatillas 7=zapatos
                }catch(Exception e){
                    System.out.println("error");
                }
            break;
            case "-todo":
                try{
                    BufferedReader br = new BufferedReader(new FileReader(args[1]));
                    Imagen img; 
                    DBLoader mc = new DBLoader();
                    mc.loadDBFromPath("./db");
                    List<ClasificadorFuerte> fuerte = new ArrayList<>();
                    double aciertos = 0;
                    int total = 0; 
                    for(int i=0; i<8; i++){
                        cargarDatos(br,fuerte,i);
                    }
                    for(int i=0; i<8; i++){
                         ArrayList d0imgs = mc.getImageDatabaseForDigit(i);
                         total += d0imgs.size();
                         
                         for(int j=0; j<d0imgs.size();j++){
                             img = (Imagen) d0imgs.get(j);
                             List<Double> listaRes = new ArrayList<>();
                             for(int k=0; k<8; k++){
                                double res = fuerte.get(k).aplicarClasificador(img);
                                listaRes.add(res);
                               // System.out.print(k+":"+res+" ");
                             }
                             //System.out.println();
                             double auxConfianza =Double.MIN_VALUE;
                             int categoria = -1;
                             for(int k=0; k<listaRes.size();k++){
                                 if(listaRes.get(k)==1.0 && auxConfianza < fuerte.get(k).getConfiabilidad()){
                                     categoria = k;
                                     auxConfianza = fuerte.get(k).getConfiabilidad();
                                 }
                             }
                             if(categoria == i)
                                 aciertos++;
                         }
                    }
                    double porcentaje = (aciertos / total) * 100;
                    System.out.println("aciertos= "+ aciertos + " total= "+ total + " porcentaje=" + porcentaje +"%");
                     
                }catch(Exception e){
                    System.err.println("error");
                }
                break;
            default:
                System.err.print("ERROR EN PARAMETROS");
                break;
        }
           
        //Accedo a las imagenes de bolsos
       /* ArrayList d0imgs = ml.getImageDatabaseForDigit(1);
        
        //Y cojo el decimo bolso de la bd
        Imagen img = (Imagen) d0imgs.get(9);
        
        //La invierto para ilustrar como acceder a los pixels y imprimo los pixeles
        //en hexadecimal
        System.out.print("Image pixels: ");
        byte imageData[] = img.getImageData();
        for (int i = 0; i < imageData.length; i++)
        {
            imageData[i] = (byte) (255 - imageData[i]);
            System.out.format("%02X ", imageData[i]);
        }
        
        //Muestro la imagen invertida
        MostrarImagen imgShow = new MostrarImagen();
        imgShow.setImage(img);
        imgShow.mostrar();*/
    }
    public static void cargarDatos(BufferedReader br, List<ClasificadorFuerte> fuertes,int i)throws Exception{
        String linea = br.readLine();//cada linea es un clasificador fuerte
        String[] confianza = linea.split(";"); // confianza;demas     
        String[] debiles = confianza[1].split("\\*");
                        fuertes.add(new ClasificadorFuerte());
                        fuertes.get(i).setConfiabilidad(Double.parseDouble(confianza[0]));
                        for(int j=0; j<debiles.length;j++){ // recorro cada hiperplano
                            HiperPlano aux = new HiperPlano();
                            String[] variables = debiles[j].split("\\|");
                            List<Double> vectorNormal = new ArrayList<>();
                            for(int k=0; k<variables.length;k++){//recorro todas las variables
                                if(k==0)// es c
                                    aux.setC(Double.parseDouble(variables[k]));
                                else if(k==1) // es alpha
                                    aux.setAlpha(Double.parseDouble(variables[k]));
                                else
                                    vectorNormal.add(Double.parseDouble(variables[k]));
                            }
                            aux.setVectorNormal(vectorNormal);
                            fuertes.get(i).getHiperplanos().add(aux);
                        }
    }
}
