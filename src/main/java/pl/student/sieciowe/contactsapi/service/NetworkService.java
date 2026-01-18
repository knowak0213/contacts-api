package pl.student.sieciowe.contactsapi.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class NetworkService {

    /**
     * TCP - Testuje połączenie TCP z zewnętrznym serwerem
     * Przykład: połączenie z serwerem HTTP (google.com:80)
     */
    public String testTcpConnection(String host, int port) {
        StringBuilder result = new StringBuilder();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5000);
            result.append("TCP Connection SUCCESS\n");
            result.append("Connected to: ").append(host).append(":").append(port).append("\n");
            result.append("Local address: ").append(socket.getLocalAddress()).append("\n");
            result.append("Remote address: ").append(socket.getRemoteSocketAddress());
        } catch (IOException e) {
            result.append("TCP Connection FAILED: ").append(e.getMessage());
        }
        return result.toString();
    }

    public String testUdpConnection(String host, int port, String message) {
        StringBuilder result = new StringBuilder();
        try (DatagramSocket socket = new DatagramSocket()) {
            // Skracamy timeout do 1 sekundy, aby nie blokować terminala
            socket.setSoTimeout(1000);

            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);
            InetAddress address = InetAddress.getByName(host);

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            result.append("UDP Packet SENT successfully.\n");
            result.append("To: ").append(host).append(":").append(port).append("\n");
            result.append("Message: ").append(message).append("\n");

            // Próba odebrania odpowiedzi (opcjonalnie)
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                result.append("Response received: ").append(response);
            } catch (SocketTimeoutException e) {
                result.append(
                        "Status: Packet sent, but no response from server (this is normal for many UDP services).");
            }
        } catch (IOException e) {
            result.append("UDP Error: ").append(e.getMessage());
        }
        return result.toString();
    }

    /**
     * FTP - Wysyła plik z kontaktami na serwer FTP
     */
    public String uploadToFtp(String host, int port, String username, String password,
            String remoteFileName, String fileContent) {
        StringBuilder result = new StringBuilder();
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(host, port);
            result.append("FTP Connected to: ").append(host).append(":").append(port).append("\n");

            boolean loginSuccess = ftpClient.login(username, password);
            if (!loginSuccess) {
                return "FTP Login FAILED";
            }
            result.append("FTP Login SUCCESS\n");

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            try (InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8))) {
                boolean uploaded = ftpClient.storeFile(remoteFileName, inputStream);
                if (uploaded) {
                    result.append("File uploaded: ").append(remoteFileName);
                } else {
                    result.append("FTP Upload FAILED");
                }
            }

            ftpClient.logout();
        } catch (IOException e) {
            result.append("FTP Error: ").append(e.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (IOException ignored) {
            }
        }
        return result.toString();
    }

    /**
     * Konwertuje listę kontaktów do formatu CSV
     */
    public String contactsToCsv(List<?> contacts) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,firstName,lastName,email,phoneNumber\n");
        // Kontakty będą serializowane przez kontroler
        return csv.toString();
    }
}
