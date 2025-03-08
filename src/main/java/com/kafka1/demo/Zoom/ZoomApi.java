package com.kafka1.demo.Zoom;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ZoomApi {
    private final Logger logger = Logger.getLogger("ZoomAPI");
    private LocalDateTime lastTimeTokenRequest = LocalDateTime.now();
    private String accessToken = null;

    public String createZoomSession(){
        if (accessToken==null || lastTimeTokenRequest.plusHours(1).isBefore(LocalDateTime.now())){
            accessToken=getAccessToken();
            lastTimeTokenRequest = LocalDateTime.now();
            if (accessToken==null) {
                logger.log(Level.WARNING,"ACCESS TOKEN IS NULL");
                return null;
            }
        }
        String url = "https://zoom.us/v2/users/me/meetings";
        String jsonResponse = sendRequest(url,getJsonForCreateZoomSession(),"application/json","Bearer "+accessToken);
        try {
            JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
            JsonObject jsonObject = jsonReader.readObject();
            return jsonObject.getString("join_url");
        }
        catch (Exception err){
            return null;
        }
}

    private String getAccessToken(){
        String url = "https://zoom.us/oauth/token?grant_type=account_credentials&account_id=EBWdlUllSAS6L35zM6vaVA";
        String contentType = "application/x-www-form-urlencoded";
        String jsonResponse = sendRequest(url,null,contentType,"Basic bDg5TWM0c1RUQTZCVTFtSWdxeUdBOjFkWTI4WXBTaEIyYmxqdzlxakVSVnNoS1VUS2lmZ2Q2");
        if (jsonResponse==null){
            logger.log(Level.WARNING,"getAccessToken is returning null");
            return null;
        }
        JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
        JsonObject jsonObject = jsonReader.readObject();
        return jsonObject.getString("access_token");
    }

    private String getJsonForCreateZoomSession() {
        return """
                {
                    "topic": "Сеанс",
                    "type": 2,
                    "start_time": "2023-10-31T10:00:00Z",
                    "duration": 40,
                    "timezone": "UTC+3",
                    "agenda": "Сеанс",
                    "settings": {
                        "host_video": true,
                        "participant_video": true,
                        "join_before_host": false,
                        "mute_upon_entry": true,
                        "watermark": false,
                        "audio": "both",
                        "auto_recording": "cloud"
                    }
                }""";
    }

    public String sendRequest(String url,String json,String contentType,String auth){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create("", MediaType.parse("text/plain"));
        if (json!=null) {
            body = RequestBody.create(
                    json,
                    MediaType.parse(contentType + "; charset=utf-8")
            );
        }
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",auth)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body()!=null) {
                return response.body().string();
            }
            else {
                logger.log(Level.WARNING,"Response is null");
                return null;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING,e.toString());
            return null;
        }
    }
}

