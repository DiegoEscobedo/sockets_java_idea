package servidor.udp;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServidorVideoLLamadaUDP extends Thread {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private final int port;
    private static final int MAX_PACKET_SIZE = 60000;  // 60 KB, para asegurar que los paquetes no sean demasiado grandes

    public ServidorVideoLLamadaUDP(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port);

            byte[] buffer = new byte[MAX_PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet);
                byte[] data = packet.getData();
                int length = packet.getLength();

                MatOfByte mob = new MatOfByte();
                mob.fromArray(data);
                Mat frame = Imgcodecs.imdecode(mob, Imgcodecs.IMREAD_COLOR);

                if (!frame.empty()) {
                    HighGui.imshow("Servidor Video", frame);
                    HighGui.waitKey(1);
                }
            }
        } catch (SocketException e) {
            System.err.println("Socket error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en la videollamada del servidor: " + e.getMessage());
        }
    }
}
