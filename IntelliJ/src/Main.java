import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws Exception {
        String response = logIn();
        JSONObject jsonObject = new JSONObject(response);
        int sessionId = jsonObject.getInt("sessionId");

        task1(sessionId);
        task2(sessionId);
        task3(sessionId);
        task4(sessionId);

        secretTask(sessionId);

        recieveFeedback(sessionId);
    }

    private String BASE_URL; // Base URL (address) of the server

    /**
     * Create an HTTP POST
     *
     * @param host Will send request to this host: IP address or domain
     * @param port Will use this port
     */
    public Main(String host, int port) {
        BASE_URL = "http://" + host + ":" + port + "/";
    }

    /**
     * Post email and phone number to the web server.
     */
    private static String logIn(){
        String url = "dkrest/auth";
        String phone = "47806366";
        String email = "emilva@stud.ntnu.no";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("phone", phone);

        String response;
        response =  sendPost(url, jsonObject);
        return response;
    }

    private static void task1(int sessionID){
        String response = sendGet("dkrest/gettask/1?sessionId=" + sessionID);
        System.out.println(response);

        JSONObject send = new JSONObject();
        send.put("msg", "Hello");
        send.put("sessionId", sessionID);
        response = sendPost("dkrest/solve", send);
        System.out.println(response);
    }

    private static void task2(int sessionId){
        String response = sendGet("dkrest/gettask/2?sessionId=" + sessionId);
        System.out.println(response);

        JSONObject jObject = new JSONObject(response);
        response = (String) jObject.getJSONArray("arguments").get(0);

        JSONObject send = new JSONObject();
        send.put("msg", response);
        send.put("sessionId", sessionId);
        response = sendPost("dkrest/solve", send);
        System.out.println(response);
    }

    private static void task3(int sessionId){
        String response = sendGet("dkrest/gettask/3?sessionId=" + sessionId);
        System.out.println(response);

        JSONObject jObject = new JSONObject(response);
        JSONArray array = jObject.getJSONArray("arguments");
        int mul = 1;

        int arrayCount = array.length();
        for(int i = 0; i < arrayCount; i++){
            int number;
            number = array.getInt(i);
            mul *= number;
        }
        System.out.println(mul);

        JSONObject send = new JSONObject();
        send.put("result", mul);
        send.put("sessionId", sessionId);
        response = sendPost("dkrest/solve", send);
        System.out.println(response);
    }


    private static void task4(int sessionId) throws Exception {
        String respons;
        respons = sendGet("dkrest/gettask/4?sessionId=" + sessionId);
        System.out.println(respons);

        JSONObject jOb = new JSONObject(respons);
        JSONArray array = jOb.getJSONArray("arguments");


        for (int a = 0; a <= 9; a++) {
            for (int b = 0; b <= 9; b++) {
                for (int c = 0; c <= 9; c++) {
                    for (int d = 0; d <= 9; d++) {
                        String pin = "" + a + "" + b + "" + c + "" + d;
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte[] hashInBytes = md.digest(pin.getBytes(StandardCharsets.UTF_8));

                        StringBuilder sb = new StringBuilder();
                        for (byte g : hashInBytes) {
                            sb.append(String.format("%02x", g));
                        }
                        if (array.get(0).equals(sb.toString())) {

                            JSONObject send = new JSONObject();
                            send.put("pin", pin);
                            send.put("sessionId", sessionId);
                            respons = sendPost("dkrest/solve", send);
                            System.out.println(respons);
                        }

                    }
                }
            }
        }

    }

    private static String secretTask(int sessionId) {
        int code = (int) Math.sqrt(4064256);
        String response = sendGet("dkrest/gettask/" + code +"?sessionId=" + sessionId);
        JSONObject jsonObject = new JSONObject(response);
        JSONArray arguments = jsonObject.getJSONArray("arguments");

        String ip = arguments.getString(0);
        //String mask = arguments.getString(1);

        Random rand = new Random();
        int n = rand.nextInt(255) + 1;
        System.out.println(n);

        String[] splitIp = ip.split("\\.");


        String newIP = splitIp[0] + "." + splitIp[1] + "." + splitIp[2] + "." + Integer.toString(n);

        System.out.println("Sending: " + newIP);

        JSONObject json = new JSONObject();
        json.put("sessionId", sessionId);
        json.put("ip", newIP);
        //json.put("mask", mask);
        return sendPost("dkrest/solve", json);
    }
    private static void recieveFeedback(int sessionId){
        String response;
        response = sendGet("dkrest/results/" + sessionId);
        System.out.println(response);
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
                //System.out.println(responseBody);
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
     * Send HTTP POST
     *
     * @param path Relative path in the API.
     * @param jsonData The data in JSON format that will be posted to the server
     */
    static String sendPost(String path, JSONObject jsonData) {
        String returnValue;
        String BASE_URL = "http://" + "104.248.47.74" + ":" + "80" + "/";
        try {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP POST to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Server reached");

                // Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                String responseBody = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                //System.out.println(responseBody);
                returnValue = responseBody;
            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
                returnValue = null;
            }
        } catch (ProtocolException e) {
            System.out.println("Protocol nto supported by the server");
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

