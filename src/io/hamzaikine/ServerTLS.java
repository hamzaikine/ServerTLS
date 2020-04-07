package io.hamzaikine;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerTLS {

    private static final String[] protocols = new String[] {"TLSv1.3"};
    private static final String[] cipher_suites = new String[] {"TLS_AES_128_GCM_SHA256"};
    private static final int PORT = 8443;

    public static void main(String[] args) {
        // we load server keystore and truststore
        System.setProperty("javax.net.ssl.keyStore","src/resources/mykeystore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword","secretpass");
        System.setProperty("javax.net.ssl.trustStore","src/resources/mykeystore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword","secretpass");

        final SSLServerSocket sslServerSocket;

        try {
            sslServerSocket = createSSLServerSocket();
            System.out.printf("server started and listening on port %d%n", sslServerSocket.getLocalPort());
            sslServerSocket.setNeedClientAuth(true);
            Socket socket = sslServerSocket.accept();
            System.out.printf("Client Address: %s accepted%n",socket.getInetAddress().getHostAddress());

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            try (BufferedReader bufferedReader =
                         new BufferedReader(
                                 new InputStreamReader(socket.getInputStream()))) {
                String line;
                while((line = bufferedReader.readLine()) != null){
                    System.out.println(line);
                    out.println("Server received: "+line + " from: "+socket.getInetAddress().getHostName());
                }
            }
            System.out.println("Closed");

        } catch (IOException ex) {
            Logger.getLogger(ServerTLS.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }


    private static SSLServerSocket createSSLServerSocket() throws IOException {
        SSLServerSocket sslServerSocket = (SSLServerSocket)
                SSLServerSocketFactory.getDefault().createServerSocket(PORT);
        sslServerSocket.setEnabledProtocols(protocols);
        sslServerSocket.setEnabledCipherSuites(cipher_suites);

        return  sslServerSocket;
    }


}
