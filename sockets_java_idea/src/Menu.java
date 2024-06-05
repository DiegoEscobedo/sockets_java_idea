import cliente.tcp.ClienteTCP;
import cliente.udp.ClienteUDP;
import cliente.udp.ClienteVideoLLamadaUDP;
import servidor.tcp.ServidorTCP;
import servidor.udp.ServidorUDP;
import servidor.udp.ServidorVideoLLamadaUDP;
import datos.EntradaSalida;
import java.util.ArrayList;

import cliente.objetos.Contacto;

public class Menu {
    public static void main(String[] args) throws Exception {
        ArrayList<Contacto> contactos = new ArrayList<>();
        contactos.add(new Contacto( "192.168.18.1", "Diego"));
        contactos.add(new Contacto(null, "Jose"));
        // Agrega más contactos 
        String opcion;

        while (true) {
            mostrarMenuPrincipal();
            opcion = EntradaSalida.consolaCadenas();

            if (opcion.equals("1")) {
                mostrarMenuContactos(contactos);
            } else if (opcion.equals("2")) {
                EntradaSalida.mostrarMensaje("Adios, gracias por usar este chat chafa\n");
                System.exit(0);
            }
        }
    }

    private static void mostrarMenuPrincipal() {
        EntradaSalida.mostrarMensaje("Menu del Chat (Escriba un numero) \n");
        EntradaSalida.mostrarMensaje("1 para Ver Contactos\n");
        EntradaSalida.mostrarMensaje("2 para cerrar el chat\n");
    }

    private static void mostrarMenuContactos(ArrayList<Contacto> contactos) throws Exception {
        String opcion;
        EntradaSalida.mostrarMensaje("Contactos (Selecciona un contacto)\n");
        for (int i = 0; i < contactos.size(); i++) {
            EntradaSalida.mostrarMensaje((i + 1) + " para chatear con: " + contactos.get(i).toString() + "\n");
        }
        EntradaSalida.mostrarMensaje("Cualquier otro numero para regresar\n");
        opcion = EntradaSalida.consolaCadenas();

        int opcionSeleccionada = Integer.parseInt(opcion);
        if (opcionSeleccionada >= 1 && opcionSeleccionada <= contactos.size()) {
            String ipContacto = contactos.get(opcionSeleccionada - 1).getIp();
            mostrarMenuAccionesContacto(ipContacto);
        } else {
            EntradaSalida.mostrarMensaje("Adios, gracias por usar este chat chafa\n");
            System.exit(0);
        }
    }

    private static void mostrarMenuAccionesContacto(String ipContacto) throws Exception {
        String opcion;
        EntradaSalida.mostrarMensaje("Que desea hacer?\n");
        EntradaSalida.mostrarMensaje("1 para mandar mensajes\n");
        EntradaSalida.mostrarMensaje("2 para mandar un archivo\n");
        EntradaSalida.mostrarMensaje("3 para videollamada\n");
        EntradaSalida.mostrarMensaje("exit para regresar\n");
        opcion = EntradaSalida.consolaCadenas();

        switch (opcion) {
            case "1":
                // Crear el servidor UDP
                ServidorUDP servidorUDP = new ServidorUDP(50000);
                ClienteUDP clienteUDP = new ClienteUDP(ipContacto, 50000); // Puerto UDP para mensajes
                clienteUDP.inicia();
                servidorUDP.inicia();
                break;
            case "2":
                // Crear el servidor TCP
                ServidorTCP servidorTCP = new ServidorTCP(60000);
                servidorTCP.inicia();
                ClienteTCP clienteTCP = new ClienteTCP(ipContacto, 60000); // Puerto TCP para archivos
                clienteTCP.iniciaArch();
                break;
            case "3":
                iniciarVideollamada(ipContacto);
                break;
            case "exit":
                // No hace nada, simplemente permite salir del switch
                break;
            default:
                EntradaSalida.mostrarMensaje("Opción no válida. Por favor, intenta de nuevo.\n");
                break;
        }
    }

    private static void iniciarVideollamada(String ipContacto) {
        // Iniciar el servidor de videollamada
        Thread servidorVideoThread = new Thread(() -> {
            try {
                ServidorVideoLLamadaUDP servidorVideo = new ServidorVideoLLamadaUDP(7000);
                servidorVideo.start();
            } catch (Exception e) {
                EntradaSalida.mostrarMensaje("Error iniciando el servidor de videollamada: " + e.getMessage() + "\n");
            }
        });
        servidorVideoThread.start();

        // Iniciar el cliente de videollamada
        Thread clienteVideoThread = new Thread(() -> {
            try {
                ClienteVideoLLamadaUDP clienteVideo = new ClienteVideoLLamadaUDP(ipContacto, 7000);
                clienteVideo.start();
            } catch (Exception e) {
                EntradaSalida.mostrarMensaje("Error iniciando el cliente de videollamada: " + e.getMessage() + "\n");
            }
        });
        clienteVideoThread.start();
    }
}
