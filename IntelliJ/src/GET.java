import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * An example showing how to send HTTP GET and read the response from the server
 */
public class GET {

    public static void main(String[] args) {
        GET example = new GET("104.248.47.74", 80);
        example.doExampleGet();
    }

    private String BASE_URL; // Base URL (address) of the server

    /**
     * Create an HTTP GET example
     *
     * @param host Will send request to this host: IP address or domain
     * @param port Will use this port
     */
    public GET(String host, int port) {
        BASE_URL = "http://" + host + ":" + port + "/";
    }

    /**
     * Send an HTTP GET to a specific path on the web server
     */
    public void doExampleGet() {
        sendGet("dkrest/test/get2");
    }

    /**
     * Send HTTP GET
     *
     * @param path     Relative path in the API.
     */
    static String sendGet(String path) {
        String returnValue;
        String BASE_URL = "http://" + "104.248.47.74" + ":" + "80" + "/";
        try {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP GET to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Server reached");
                // Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                String responseBody = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                System.out.println(responseBody);
                returnValue = responseBody;
            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
                returnValue = null;
            }
        } catch (ProtocolException e) {
            System.out.println("Protocol not supported by the server");
            returnValue = null;
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            returnValue = null;
        }
        return returnValue;
    }

    /**
     * Read the whole content from an InputStream, return it as a string
     * @param is Inputstream to read the body from
     * @return The whole body as a string
     */
    private static String convertStreamToString(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append('\n');
            }
        } catch (IOException ex) {
            System.out.println("Could not read the data from HTTP response: " + ex.getMessage());
        }
        return response.toString();
    }
}