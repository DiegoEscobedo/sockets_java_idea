package datos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EntradaSalida {
    public static void mostrarMensaje(String mensaje){
        System.out.print(mensaje);
    }

    @SuppressWarnings("finally")
    public static String consolaCadenas(){
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader buffer = new BufferedReader(isr);
        String cadenaEntrada="";

        try{
            cadenaEntrada= buffer.readLine();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            return cadenaEntrada;
        }
    }

    @SuppressWarnings("finally")
    public static Double consolaDouble(){
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader buffer = new BufferedReader(isr);
        double entrada=0.0;

        try{
            entrada= Double.parseDouble(buffer.readLine());
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            return entrada;
        }
    }
}
