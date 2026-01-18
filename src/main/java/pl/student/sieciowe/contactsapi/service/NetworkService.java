package pl.student.sieciowe.contactsapi.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.net.Socket;

@Service
public class NetworkService {

    public void connectToTcpServer(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to " + host + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
