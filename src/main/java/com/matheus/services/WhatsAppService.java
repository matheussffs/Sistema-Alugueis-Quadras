package com.matheus.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.ejb.Stateless;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Stateless
public class WhatsAppService {

    private static final String TOKEN = ""; 
    
    private static final String PHONE_NUMBER_ID = "112654291858983"; 
    
    private static final String VERSION = "v17.0"; 

    private final HttpClient client;
    private final Gson gson;

    public WhatsAppService() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public boolean enviarLembrete(String telefoneCliente) {
        try {
            String url = "https://graph.facebook.com/" + VERSION + "/" + PHONE_NUMBER_ID + "/messages";

            String telefoneFormatado = "55" + telefoneCliente.replaceAll("\\D", ""); 

            JsonObject json = new JsonObject();
            json.addProperty("messaging_product", "whatsapp");
            json.addProperty("to", telefoneFormatado);
            json.addProperty("type", "template");

            JsonObject template = new JsonObject();
            template.addProperty("name", "hello_world"); 
            
            JsonObject language = new JsonObject();
            language.addProperty("code", "en_US");
            template.add("language", language);

            json.add("template", template);

            String jsonBody = gson.toJson(json);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println(">>> SUCESSO! WhatsApp enviado para: " + telefoneFormatado);
                return true;
            } else {
                System.err.println(">>> ERRO META: " + response.statusCode() + " - " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.err.println(">>> ERRO JAVA: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}