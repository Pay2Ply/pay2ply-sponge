package com.pay2ply.sdk;

import com.google.gson.Gson;
import com.pay2ply.sdk.dispense.Dispense;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SDK {
  public String API = "https://api.pay2ply.com/";

  public String token;

  public String getAPI() {
    return API;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Dispense[] getDispenses() {
    String response = get();
    return new Gson().fromJson(response, Dispense[].class);
  }

  public void updateDispense(String username, int id) {
    update(username, id);
  }

  public String get() {
    try {
      HttpsURLConnection connection = (HttpsURLConnection) new URL(getAPI() + "plugin").openConnection();

      connection.setRequestMethod("GET");
      connection.setRequestProperty("Authorization", getToken());
      connection.setRequestProperty("User-Agent", "Java client");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setUseCaches(false);
      connection.setAllowUserInteraction(false);
      connection.setConnectTimeout(1500);
      connection.setReadTimeout(3000);
      connection.connect();

      if (connection.getResponseCode() >= 500) {
        System.out.println("[Pay2Ply] A API da Pay2Ply encontra-se indisponível no momento.");
      }

      if (connection.getResponseCode() == 423) {
        System.out.println("[Pay2Ply] O pagamento de sua loja encontra-se pendente.");
      }

      if (connection.getResponseCode() == 400) {
        System.out.println("[Pay2Ply] O token do servidor não foi encontrado na API.");
      }

      if (connection.getResponseCode() == 401) {
        System.out.println("[Pay2Ply] O token do servidor não foi encontrado na API.");
      }

      if (connection.getResponseCode() == 403) {
        System.out.println("[Pay2Ply] O IP do servidor não é o mesmo deste servidor, configure-o.");
      }

      if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201 || connection.getResponseCode() == 204) {
        String line;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
          stringBuilder.append(line).append("\n");
        }

        bufferedReader.close();

        connection.disconnect();

        return stringBuilder.toString();
      }

      connection.disconnect();
    } catch (IOException ex) {
      Logger.getLogger(SDK.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public void update(String username, int id) {
    try {
      HttpsURLConnection connection = (HttpsURLConnection) new URL(getAPI() + "plugin/actived/" + username + "/" + id).openConnection();

      connection.setRequestMethod("GET");
      connection.setRequestProperty("Authorization", getToken());
      connection.setRequestProperty("User-Agent", "Java client");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setUseCaches(false);
      connection.setAllowUserInteraction(false);
      connection.setConnectTimeout(1500);
      connection.setReadTimeout(3000);
      connection.connect();

      if (connection.getResponseCode() >= 500) {
        System.out.println("[Pay2Ply] A API da Pay2Ply encontra-se indisponível no momento.");
      }

      if (connection.getResponseCode() == 423) {
        System.out.println("[Pay2Ply] O pagamento de sua loja encontra-se pendente.");
      }

      if (connection.getResponseCode() == 400) {
        System.out.println("[Pay2Ply] O token do servidor não foi encontrado na API.");
      }

      if (connection.getResponseCode() == 401) {
        System.out.println("[Pay2Ply] O token do servidor não foi encontrado na API.");
      }

      if (connection.getResponseCode() == 403) {
        System.out.println("[Pay2Ply] O IP do servidor não é o mesmo deste servidor, configure-o.");
      }

      connection.disconnect();
    } catch (IOException ex) {
      Logger.getLogger(SDK.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
