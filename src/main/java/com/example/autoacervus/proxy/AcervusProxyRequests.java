package com.example.autoacervus.proxy;

import com.example.autoacervus.encryption.AES256;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;

import javax.security.auth.login.LoginException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

@Component
public class AcervusProxyRequests implements AcervusProxy {
  private final Logger logger = Logger.getLogger(AcervusProxyRequests.class.getName());
  private CookieStore cookieStore = new BasicCookieStore();

  public AcervusProxyRequests() {
  }

  private User user;

  @SuppressWarnings("deprecation")
  @Override
  public boolean login(User user) throws LoginException {
    this.user = user;
    // Logout previously logged in user.
    this.logout();

    // Forge a login request to the Acervus API and save cookies.
    final JSONObject jsonPayload = new JSONObject();
    jsonPayload.put("identificacao", user.getEmailDac());

    String password = user.getSbuPassword();
    jsonPayload.put("senha", password);

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

      throw new LoginException("Invalid credentials");
    } catch (Exception e) {
      this.logger.severe("[Login] Failed to login: " + e.getMessage());
    }

    return false;
  }

  @SuppressWarnings("deprecation")
  public void logout() {
    this.logger.info("[Logout] Logging out and clearing previous cookies...");
    this.cookieStore.clear();
    // Forge a logout request to the Acervus API.
    try (CloseableHttpClient httpClient = HttpClients.custom()
        .setDefaultCookieStore(cookieStore)
        .build()) {

      HttpGet get = new HttpGet("https://acervus.unicamp.br/logout");
      this.logger.info("[Logout] Sending logout request...");
      try (CloseableHttpResponse response = httpClient.execute(get)) {
        final int responseCode = response.getCode();
        if (responseCode != 302) {
          this.logger.warning("[Logout] HTTP status code is not 302 (received: " + responseCode
              + "). Ignoring, as it seems there is no one logged in.");
        }
      }
    } catch (Exception e) {
      this.logger.severe("[Logout] Failed to logout (probably not logged in): " + e.getMessage());
    }
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
        if (entity == null) {
          this.logger.severe("[IsLoggedIn] Response entity is null.");
          return false;
        }

        String result = EntityUtils.toString(entity);
        JSONObject jsonObject = new JSONObject(result);
        if (!jsonObject.has("resultado")) {
          this.logger.severe("[IsLoggedIn] JSON response does not contain 'resultado' key.");
          return false;
        }

        boolean isLoggedIn = jsonObject.getBoolean("resultado");
        return isLoggedIn;
      }
    } catch (Exception e) {
      this.logger.severe("[IsLoggedIn] Failed to check for login: " + e.getMessage());
    }

    return false;
  }

  @SuppressWarnings("deprecation")
  @Override
  public List<BorrowedBook> getBorrowedBooks() {
    LinkedList<BorrowedBook> borrowedBooks = new LinkedList<>();
    // Forge a request to the Acervus API to list currently borrowed books
    try (CloseableHttpClient httpClient = HttpClients.custom()
        .setDefaultCookieStore(cookieStore)
        .build()) {

      HttpPost post = new HttpPost("https://acervus.unicamp.br/emprestimo/ListarCirculacoesEmAberto");
      post.setEntity(new StringEntity("{\"sort\": \"DataEmprestimo-desc\"}"));
      post.setHeader("Content-Type", "application/json; charset=UTF-8");

      this.logger.info("[GetBorrowedBooks] Sending request...");
      try (CloseableHttpResponse response = httpClient.execute(post)) {
        final int responseCode = response.getCode();
        if (responseCode != 200) {
          this.logger.severe(
              "[GetBorrowedBooks] HTTP status code is not 200 (received: " + responseCode + "). Returning empty list.");
          return borrowedBooks;
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) {
          this.logger.severe("[GetBorrowedBooks] Response entity is null. Returning empty list.");
          return borrowedBooks;
        }

        String result = EntityUtils.toString(entity);
        JSONObject jsonObject = new JSONObject(result);
        if (!jsonObject.has("Result")) {
          this.logger.severe("[GetBorrowedBooks] JSON response does not contain 'Result' key. Returning empty list.");
          return borrowedBooks;
        }
        JSONObject resultObject = jsonObject.getJSONObject("Result");
        if (!resultObject.has("Data")) {
          this.logger.severe("[GetBorrowedBooks] JSON response does not contain 'Data' key. Returning empty list.");
          return borrowedBooks;
        }
        JSONArray bookArray;
        try {
          bookArray = resultObject.getJSONArray("Data");
        } catch (Exception e) {
          this.logger.severe(
              "[GetBorrowedBooks] JSON response does not contain 'Data' key or it does not seem to be an array. Returning empty list.");
          return borrowedBooks;
        }

        for (int i = 0; i < bookArray.length(); i++) {
          JSONObject bookEntry = bookArray.getJSONObject(i);
          int bookCode = bookEntry.getInt("Codigo");
          int bookRegistryCode = bookEntry.getInt("CodigoRegistro");
          String bookTitle = bookEntry.getString("Titulo");
          String dateString = bookEntry.getString("DataDevolucaoPrevista").split("T")[0];
          LocalDate expectedReturnDate = LocalDate.parse(dateString);
          BorrowedBook borrowedBook = new BorrowedBook(
              this.user, bookTitle,
              bookCode, bookRegistryCode, expectedReturnDate);
          borrowedBooks.add(borrowedBook);
        }
      }

      this.logger.info("[GetBorrowedBooks] Request was successfully parsed.");
    } catch (Exception e) {
      this.logger.severe("[GetBorrowedBooks] Failed to retrieve borrowed books: " + e.getMessage());
      borrowedBooks.clear();
    }

    return borrowedBooks;
  };

  @SuppressWarnings("deprecation")
  @Override
  public boolean renewBooks(List<BorrowedBook> books) {
    if (books.isEmpty()) {
      this.logger.warning("[RenewBooks] No books to renew.");
      return false;
    }

    // Forge a renewal request to the Acervus API.
    JSONArray renewArray = new JSONArray();
    for (int i = 0; i < books.size(); i++) {
      JSONObject bookPayload = new JSONObject();
      bookPayload.put("Codigo", books.get(i).getCode());
      bookPayload.put("CodigoRegistro", books.get(i).getRegistryCode());
      bookPayload.put("Titulo", books.get(i).getTitle());
      renewArray.put(bookPayload);
    }

    try (CloseableHttpClient httpClient = HttpClients.custom()
        .setDefaultCookieStore(cookieStore)
        .build()) {

      HttpPost post = new HttpPost("https://acervus.unicamp.br/emprestimo/renovar");
      post.setEntity(new StringEntity(renewArray.toString()));
      post.setHeader("Content-Type", "application/json; charset=UTF-8");

      this.logger.info("[RenewBooks] Sending renew request with JSON payload...");
      try (CloseableHttpResponse response = httpClient.execute(post)) {
        final int responseCode = response.getCode();
        if (responseCode != 200) {
          this.logger.warning("[RenewBooks] HTTP status code is not 200 (received: " + responseCode + ")");
          return false;
        }

        this.logger.info("[RenewBooks] Renew request was successfully accepted. Checking results...");

        HttpEntity entity = response.getEntity();
        if (entity == null) {
          this.logger.severe("[RenewBooks] Response entity is null.");
          return false;
        }

        String result = EntityUtils.toString(entity);
        JSONObject jsonObject = new JSONObject(result);
        if (!jsonObject.has("CirculacaoRenovadaSet")) {
          this.logger.severe("[RenewBooks] JSON response does not contain 'CirculacaoRenovadaSet' key.");
          return false;
        }
        JSONArray renewedBooks;
        try {
          renewedBooks = jsonObject.getJSONArray("CirculacaoRenovadaSet");
        } catch (Exception e) {
          this.logger.severe(
              "[RenewBooks] JSON response does not contain 'CirculacaoRenovadaSet' key or it does not seem to be an array.");
          return false;
        }

        for (int i = 0; i < renewedBooks.length(); i++) {
          JSONObject book = renewedBooks.getJSONObject(i);
          if (!book.has("Titulo")) {
            continue;
          }

          if (!book.has("Resultado")) {
            this.logger
                .warning("[RenewBooks] Book \"" + book.getString("Titulo") + "\" does not contain 'Resultado' key.");
            continue;
          }

          if (!book.getString("Resultado").equals("Empréstimo renovado.")) {
            this.logger.warning("[RenewBooks] Book \"" + book.getString("Titulo") + "\" was not renewed.");
            continue;
          }

          this.logger.info("[RenewBooks] Book \"" + book.getString("Titulo") + "\" was successfully renewed.");
        }

        return true;
      }
    } catch (Exception e) {
      this.logger.severe("[RenewBooks] Failed to login: " + e.getMessage());
    }

    return false;
  }

  @Override
  public List<BorrowedBook> renewBooksDueToday() throws LoginException {
    List<BorrowedBook> booksDueToday = new LinkedList<>();

    for (BorrowedBook book : this.getBorrowedBooks()) {
      if (book.getExpectedReturnDate().equals(LocalDate.now())) {
        booksDueToday.add(book);
      }
    }

    System.out.println(booksDueToday);

    this.renewBooks(booksDueToday);
    return booksDueToday;
  }
}
