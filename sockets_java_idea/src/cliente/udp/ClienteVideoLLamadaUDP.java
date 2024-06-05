package cliente.udp;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClienteVideoLLamadaUDP extends Thread {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private final String serverAddress;
    private final int serverPort;
    private static final int MAX_PACKET_SIZE = 60000;  // 60 KB, para asegurarnos de no exceder el tamaño máximo de paquete UDP

    public ClienteVideoLLamadaUDP(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Error: No se puede abrir la cámara.");
            return;
        }

        Mat frame = new Mat();
        MatOfByte mem = new MatOfByte();

        try {
            DatagramSocket socket = new DatagramSocket();

            while (true) {
                camera.read(frame);
                Imgproc.resize(frame, frame, new org.opencv.core.Size(640, 480));
                Imgcodecs.imencode(".jpg", frame, mem);

                byte[] data = mem.toArray();
                int totalPackets = (int) Math.ceil(data.length / (double) MAX_PACKET_SIZE);

                for (int i = 0; i < totalPackets; i++) {
                    int start = i * MAX_PACKET_SIZE;
                    int end = Math.min(data.length, start + MAX_PACKET_SIZE);
                    byte[] packetData = new byte[end - start];
                    System.arraycopy(data, start, packetData, 0, packetData.length);

                    DatagramPacket packet = new DatagramPacket(packetData, packetData.length, InetAddress.getByName(serverAddress), serverPort);
                    socket.send(packet);
                }
            }
        } catch (SocketException e) {
            System.err.println("Socket error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en la videollamada del cliente: " + e.getMessage());
        }
    }
}
