package cliente.udp;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ClienteVideoLLamadaUDP extends Thread {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final String serverAddress;
    private final int port;

    public ClienteVideoLLamadaUDP(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName(serverAddress));
            byte[] sizeBuffer = new byte[4];
            DatagramPacket sizePacket = new DatagramPacket(sizeBuffer, sizeBuffer.length);

            while (true) {
                // Recibir el tamaño del frame primero
                socket.receive(sizePacket);
                int frameSize = ByteBuffer.wrap(sizePacket.getData()).getInt();

                // Recibir el frame en fragmentos
                byte[] frameBuffer = new byte[frameSize];
                int offset = 0;
                int packetSize = 60000; // Tamaño de cada fragmento
                while (offset < frameSize) {
                    byte[] buffer = new byte[packetSize];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    int length = Math.min(packetSize, frameSize - offset);
                    System.arraycopy(packet.getData(), 0, frameBuffer, offset, length);
                    offset += length;
                }

                MatOfByte mob = new MatOfByte(frameBuffer);
                Mat frame = Imgcodecs.imdecode(mob, Imgcodecs.IMREAD_COLOR);
                HighGui.imshow("Video", frame);
                HighGui.waitKey(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
