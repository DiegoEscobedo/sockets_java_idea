package servidor.udp;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ServidorVideoLLamadaUDP {
    private static final int PUERTO = 50001;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("localhost");

        VideoCapture camera = new VideoCapture(0); // Usar cámara por defecto
        if (!camera.isOpened()) {
            System.out.println("Error al abrir la cámara.");
            return;
        }

        Mat frame = new Mat();
        byte[] byteArray;
        while (true) {
            camera.read(frame);
            if (!frame.empty()) {
                Imgproc.resize(frame, frame, new Size(640, 480)); // Cambiar el tamaño del frame si es necesario
                MatOfByte buffer = new MatOfByte();
                Imgcodecs.imencode(".jpg", frame, buffer);
                byteArray = buffer.toArray();

                DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, address, PUERTO);
                socket.send(packet);
            }
        }
    }
}
