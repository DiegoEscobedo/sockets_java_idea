package servidor.tcp;

import java.io.File;

import servidor.tcp.ServidorEscuchaTCP;

public class ServidorTCP{
    protected final int PUERTO_SERVER;
    
    public ServidorTCP(int puertoS){
        PUERTO_SERVER=puertoS;
    }
    
    public void iniciar()throws Exception{
        ServidorEscuchaTCP servidorTCP=new ServidorEscuchaTCP(PUERTO_SERVER);
        
        servidorTCP.start();
    }

    public void inicia() throws Exception {
        ServidorEscuchaTCP servidorTCP = new ServidorEscuchaTCP(PUERTO_SERVER);
        servidorTCP.start();
    }

}
