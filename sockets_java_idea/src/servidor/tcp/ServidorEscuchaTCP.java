package servidor.tcp;

import datos.EntradaSalida;
import datos.Mensaje;

import java.net.*;
//importar la libreria java.net
 
import java.io.*;
//importar la libreria java.io
// declaramos la clase servidortcp
 
public class ServidorEscuchaTCP extends Thread {
    // declaramos un objeto ServerSocket para realizar la comunicación
    protected ServerSocket socket;
    protected Socket socket_cli;
    protected final int PUERTO_SERVER;
    protected final String RUTA_DESTINO = "/archivos_recibidos";

    public ServidorEscuchaTCP(int puertoS)throws Exception{
        PUERTO_SERVER=puertoS;
        // Instanciamos un ServerSocket con la dirección del destino y el
        // puerto que vamos a utilizar para la comunicación

        socket = new ServerSocket(PUERTO_SERVER);
    }
    // método principal main de la clase
    public void run2() {
        // Declaramos un bloque try y catch para controlar la ejecución del subprograma
        try {
            // Creamos un socket_cli al que le pasamos el contenido del objeto socket después
            // de ejecutar la función accept que nos permitirá aceptar conexiones de clientes
            EntradaSalida.mostrarMensaje("Servidor escuchando...\n");
            socket_cli = socket.accept();

            // Creamos un bucle do while en el que recogemos el mensaje
            // que nos ha enviado el cliente y después lo mostramos
            // por consola
            EntradaSalida.mostrarMensaje("Servidor conectado con cliente "+
                    socket_cli.getInetAddress()+ ":"+socket_cli.getPort()+"...\n");
            do {
                Mensaje mensajeObj=recibeMensaje();
            } while (true);
        }
        // utilizamos el catch para capturar los errores que puedan surgir
        catch (Exception e) {

            // si existen errores los mostrará en la consola y después saldrá del
            // programa
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    private Mensaje recibeMensaje() throws Exception{
        // Declaramos e instanciamos el objeto DataInputStream
        // que nos valdrá para recibir datos del cliente
        Mensaje mensajeObj=new Mensaje();

        DataInputStream in =new DataInputStream(socket_cli.getInputStream());

        String mensaje ="";

        mensaje = in.readUTF();
        mensajeObj.setMensaje(mensaje);
        mensajeObj.setAddressCliente(socket_cli.getInetAddress());
        mensajeObj.setPuertoCliente(socket_cli.getPort());

        //Imprimimos el mensaje recibido
        EntradaSalida.mostrarMensaje("Mensaje recibido \""+mensajeObj.getMensaje() +"\" de "+
                mensajeObj.getAddressCliente()+":"+mensajeObj.getPuertoCliente()+"\n");
        return mensajeObj;
    }


    private void crearCarpetaDestino() {
        File carpetaDestino = new File(RUTA_DESTINO);
        if (!carpetaDestino.exists()) {
            carpetaDestino.mkdirs(); // Crea la carpeta y cualquier directorio padre que no exista
            System.out.println("Carpeta de destino creada en: " + RUTA_DESTINO);
        } else {
            System.out.println("La carpeta de destino ya existe en: " + RUTA_DESTINO);
        }
    }

    public void run() {
        try {
            EntradaSalida.mostrarMensaje("Servidor escuchando...\n");
            socket_cli = socket.accept();

            EntradaSalida.mostrarMensaje("Servidor conectado con cliente " +
                    socket_cli.getInetAddress() + ":" + socket_cli.getPort() + "...\n");

            recibirArchivo();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void recibirArchivo() throws Exception {
        crearCarpetaDestino();
        DataInputStream in = new DataInputStream(socket_cli.getInputStream());
    
        String nombreArchivo = in.readUTF();
        long tamanoArchivo = in.readLong();
        File archivo = new File(RUTA_DESTINO, nombreArchivo);
        byte[] buffer = new byte[4096];
        OutputStream out = new FileOutputStream(archivo);
    
        int bytesLeidos;
        long startTime = System.nanoTime();
        long tiempoAnterior = startTime;
        while (tamanoArchivo > 0 && (bytesLeidos = in.read(buffer, 0, (int) Math.min(buffer.length, tamanoArchivo))) != -1) {
            out.write(buffer, 0, bytesLeidos);
            tamanoArchivo -= bytesLeidos;
    
            long tiempoActual = System.nanoTime();
            double tiempoTranscurrido = (tiempoActual - startTime) / 1e9; // Tiempo transcurrido en segundos
            double tiempoRestante = (tiempoTranscurrido / (tiempoActual - tiempoAnterior)) * tamanoArchivo / 1e9; // Tiempo restante en segundos
    
            EntradaSalida.mostrarMensaje("Tiempo transcurrido: " + tiempoTranscurrido + " segundos\n");
            EntradaSalida.mostrarMensaje("Tiempo restante: " + tiempoRestante + " segundos\n");
    
            tiempoAnterior = tiempoActual;
        }
        long endTime = System.nanoTime();
    
        out.close();
        in.close();
        socket_cli.close();
    
        double tiempoTotal = (endTime - startTime) / 1e9; // Tiempo total en segundos
        double tasaTransferencia = (archivo.length() * 8) / tiempoTotal; // Tasa de transferencia en bps
    
        EntradaSalida.mostrarMensaje("Archivo " + nombreArchivo + " recibido y guardado en " + RUTA_DESTINO + ".\n");
        EntradaSalida.mostrarMensaje("Tasa de transferencia: " + tasaTransferencia + " bps\n");
        EntradaSalida.mostrarMensaje("Tiempo total de transmisión: " + tiempoTotal + " segundos\n");
    }
    
    

    
}
