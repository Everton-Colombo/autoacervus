package com.example.autoacervus.proxy;

import com.example.autoacervus.model.entity.BorrowedBook;
// import com.example.autoacervus.model.BorrowedBookEntry;
import com.example.autoacervus.model.entity.User;

import javax.security.auth.login.LoginException;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

@Component
public class AcervusProxyRequests implements AcervusProxy {
  private final Logger logger = Logger.getLogger(AcervusProxyRequests.class.getName());
  private CookieStore cookieStore = new BasicCookieStore();

  public AcervusProxyRequests() {
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean login(User user) {
    this.logger.info("[Login] Clearing previous cookies...");
    this.cookieStore.clear();

    // Forge a login request to the Acervus API and save cookies.
    final JSONObject jsonPayload = new JSONObject();
    jsonPayload.put("identificacao", user.getEmailDac());
    jsonPayload.put("senha", user.getSbuPassword());
    try (CloseableHttpClient httpClient = HttpClients.custom()
        .setDefaultCookieStore(cookieStore)
        .build()) {

      HttpPost post = new HttpPost("https://acervus.unicamp.br/login/login");
      post.setEntity(new StringEntity(jsonPayload.toString()));
      post.setHeader("Content-Type", "application/json; charset=UTF-8");

      this.logger.info("[Login] Sending login request with JSON payload...");
      try (CloseableHttpResponse response = httpClient.execute(post)) {
        final int responseCode = response.getCode();
        if (responseCode != 200) {
          this.logger.severe("[Login] HTTP status code is not 200 (received: " + responseCode + ")");
          return false;
        }
      }

      this.logger.info("[Login] Login request was successfully accepted. Validating...");

      if (this.isLoggedIn()) {
        this.logger.info("[Login] Logged in successfully.");
        return true;
      }

      throw new LoginException("HTTP session not stored properly (major Acervus API changes?)");
    } catch (Exception e) {
      this.logger.severe("[Login] Failed to login: " + e.getMessage());
    }

    return false;
  }

  @SuppressWarnings("deprecation")
  private boolean isLoggedIn() {
    // Send request to Favorites page to check if the user is logged in. It should
    // return "resultado": true if the user is logged in.
    try (CloseableHttpClient httpClient = HttpClients.custom()
        .setDefaultCookieStore(cookieStore)
        .build()) {

      HttpPost post = new HttpPost("https://acervus.unicamp.br/Favorito/ObterMenu");
      // Send empty JSON payload. If this is omitted, the server will return a 411
      // error.
      // If you try to set Content-Length to 0, it will say the header already exists.
      // This is a workaround for Acervus' dumb API.
      post.setEntity(new StringEntity("{}"));
      post.setHeader("Content-Type", "application/json; charset=UTF-8");
      try (CloseableHttpResponse response = httpClient.execute(post)) {
        final int responseCode = response.getCode();
        if (responseCode != 200) {
          this.logger.severe("[IsLoggedIn] HTTP status code is not 200 (received: " + responseCode + ")");
          return false;
        }

        HttpEntity entity = response.getEntity();
        if (entity != null) {
          String result = EntityUtils.toString(entity);
          JSONObject jsonObject = new JSONObject(result);
          if (!jsonObject.has("resultado")) {
            this.logger.severe("[IsLoggedIn] JSON response does not contain 'resultado' key.");
            return false;
          }

          Boolean isLoggedIn = jsonObject.getBoolean("resultado");
          return isLoggedIn;
        }
      }
    } catch (Exception e) {
      this.logger.severe("[IsLoggedIn] Failed to check for login: " + e.getMessage());
    }

    return false;
  }

  @Override
  public List<BorrowedBook> getBorrowedBooks() throws LoginException {
    return null;
  };

  @Override
  public boolean renewBook(BorrowedBook book) throws LoginException {
    return false;
  };

  @Override
  public List<BorrowedBook> renewBooksDueToday() throws LoginException {
    return null;
  }
}
