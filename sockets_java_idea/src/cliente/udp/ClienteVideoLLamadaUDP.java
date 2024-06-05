import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ClienteVideoLLamadaUDP {
    private static final String SERVER = "localhost"; // Cambiar por la IP del servidor
    private static final int PUERTO = 50001;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(PUERTO);
        byte[] buffer = new byte[65536];

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            byte[] data = packet.getData();
            Mat frame = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.IMREAD_UNCHANGED);

            if (!frame.empty()) {
                Imgproc.resize(frame, frame, new Size(640, 480)); // Cambiar el tamaño del frame si es necesario
                HighGui.imshow("Cliente de videollamada", frame);
                HighGui.waitKey(1);
            }
        }
    }
}
