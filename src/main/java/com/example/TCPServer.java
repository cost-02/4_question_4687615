package com.example;

import java.io.*;
import java.net.*;

class TCPServer {
    public static void main(String[] args) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(3248);

        while (true) {
            try (Socket connectionSocket = welcomeSocket.accept()) {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                String clientCommand = inFromClient.readLine();

                if ("download".equals(clientCommand)) {
                    sendFile(connectionSocket, "file_to_send.lsp");
                } else if ("upload".equals(clientCommand)) {
                    receiveFile(connectionSocket, "received_file.lsp");
                }

                outToClient.writeBytes("Comando ricevuto: " + clientCommand + '\n');
            }
            System.out.println("Connessione terminata.");
        }
    }

    private static void sendFile(Socket socket, String filePath) throws IOException {
        File file = new File(filePath);
        byte[] mybytearray = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray, 0, mybytearray.length);
        OutputStream os = socket.getOutputStream();
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();
        System.out.println("File inviato al client.");
    }

    private static void receiveFile(Socket socket, String filePath) throws IOException {
        byte[] mybytearray = new byte[6022386];
        InputStream is = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        int current = bytesRead;

        while (bytesRead > -1) {
            bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
            if (bytesRead >= 0) current += bytesRead;
        }

        bos.write(mybytearray, 0, current);
        bos.flush();
        bos.close();
        System.out.println("File ricevuto dal client.");
    }
}
