package com.example.aillamacpphelloworld;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AiLlamacppHelloworldApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    public void testExample() throws Exception {
        String message = "tell me a joke";
        String urlString = "http://localhost:" + port + "/ai/simple-stream?message=" +
                java.net.URLEncoder.encode(message, "UTF-8");

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            try {
                JSONObject obj = new JSONObject(inputLine.replace("data:", ""));
                System.out.print(obj.getString("completion"));
                System.out.flush();
            } catch (JSONException e) {}
        }
        in.close();
	}
}
