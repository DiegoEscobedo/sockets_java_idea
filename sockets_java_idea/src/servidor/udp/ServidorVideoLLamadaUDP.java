package servidor.udp;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.MatOfByte;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ServidorVideoLLamadaUDP extends Thread {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final int port;

    public ServidorVideoLLamadaUDP(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port);
            VideoCapture camera = new VideoCapture(0);
            if (!camera.isOpened()) {
                System.out.println("Error: No se puede abrir la cámara.");
                return;
            }

            Mat frame = new Mat();
            MatOfByte buffer = new MatOfByte();
            InetAddress clientAddress = InetAddress.getByName("localhost");
            int clientPort = port;

            while (true) {
                camera.read(frame);
                Imgproc.resize(frame, frame, new Size(640, 480));
                Imgcodecs.imencode(".jpg", frame, buffer);
                byte[] data = buffer.toArray();

                ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                byteBuffer.putInt(data.length);
                byte[] sizeBytes = byteBuffer.array();

                // Enviar el tamaño del frame primero
                DatagramPacket sizePacket = new DatagramPacket(sizeBytes, sizeBytes.length, clientAddress, clientPort);
                socket.send(sizePacket);

                // Enviar el frame en fragmentos
                int offset = 0;
                int packetSize = 60000; // Tamaño de cada fragmento
                while (offset < data.length) {
                    int length = Math.min(packetSize, data.length - offset);
                    DatagramPacket packet = new DatagramPacket(data, offset, length, clientAddress, clientPort);
                    socket.send(packet);
                    offset += length;
                }
            }
        } catch (Exception e) {
            System.err.println("Error iniciando el servidor de videollamada: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
