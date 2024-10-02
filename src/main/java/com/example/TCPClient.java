package com.example;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class TCPClient {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inserisci l'indirizzo IP del server dei file:");
        String ipAdd = scanner.nextLine();
        System.out.println("Inserisci il numero di porta del server dei file:");
        int portNum = scanner.nextInt();
        scanner.nextLine(); // Consuma la newline dopo il numero

        while (true) {
            System.out.println("Inserisci 'upload', 'download' o 'close':");
            String command = scanner.nextLine();

            try (Socket clientSocket = new Socket(ipAdd, portNum)) {
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                if ("close".equals(command)) {
                    clientSocket.close();
                    break;
                } else if ("download".equals(command)) {
                    outToServer.writeBytes("download\n");
                    receiveFile(clientSocket, "downloaded_file.lsp");
                } else if ("upload".equals(command)) {
                    outToServer.writeBytes("upload\n");
                    sendFile(clientSocket, "file_to_upload.lsp");
                }

                String serverResponse = inFromServer.readLine();
                System.out.println("Risposta dal server: " + serverResponse);
            }
        }
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
        System.out.println("File ricevuto.");
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
        System.out.println("File inviato.");
    }
}