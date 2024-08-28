import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class SSLConnectionTest {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java SSLConnectionTest <NewRelicAccountID> <InsightsInsertKey>");
            System.exit(1);
        }

        String accountId = args[0];
        String insightsInsertKey = args[1];
        String httpsURL = "https://insights-collector.newrelic.com/v1/accounts/" + accountId + "/events";

        try {
            // Create a URL object
            URL url = new URL(httpsURL);

            // Create an HttpsURLConnection object
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Set SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new java.security.SecureRandom());
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            // Enable verbose SSL debugging
            System.setProperty("javax.net.debug", "ssl,handshake");

            // Set request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Insert-Key", insightsInsertKey);
            connection.setDoOutput(true);

            // Create a sample payload
            String payload = "[{\"eventType\":\"TestEvent\",\"testAttribute\":\"testValue\"}]";

            // Write payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Connect to the URL
            connection.connect();

            // Print SSL certificate information
            printCertificateInfo(connection);

            // Print response from the server
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printCertificateInfo(HttpsURLConnection connection) {
        try {
            Certificate[] certs = connection.getServerCertificates();
            for (Certificate cert : certs) {
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    System.out.println("Certificate Subject: " + x509Cert.getSubjectDN());
                    System.out.println("Certificate Issuer: " + x509Cert.getIssuerDN());
                    System.out.println("Certificate Serial Number: " + x509Cert.getSerialNumber());
                    System.out.println("Certificate Valid From: " + x509Cert.getNotBefore());
                    System.out.println("Certificate Valid Until: " + x509Cert.getNotAfter());
                    System.out.println();
                }
            }
        } catch (SSLPeerUnverifiedException e) {
            e.printStackTrace();
        }
    }
}
