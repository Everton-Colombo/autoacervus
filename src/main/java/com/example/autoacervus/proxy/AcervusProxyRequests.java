package com.example.autoacervus.proxy;

import com.example.autoacervus.model.BookRenewalResult;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;

import javax.security.auth.login.LoginException;

import com.example.autoacervus.util.HttpUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

@Component
public class AcervusProxyRequests extends AcervusProxy {
    private static final String API_IS_LOGGED_IN_ULR = "https://acervus.unicamp.br/usuario/EstaLogado";
    private static final String API_LOGOUT_URL = "https://acervus.unicamp.br/logout";
    private static final String API_LOGIN_URL = "https://acervus.unicamp.br/login/login";
    private static final String API_OBTAIN_MENU_URL = "https://acervus.unicamp.br/Favorito/ObterMenu";
    private static final String API_LIST_BORROWED_BOOKS_URL = "https://acervus.unicamp.br/emprestimo/ListarCirculacoesEmAberto";
    private static final String API_RENEWAL_URL = "https://acervus.unicamp.br/emprestimo/renovar";

    private static final String SUCCESSFULL_RENEWAL_RESULT_MSG = "Empréstimo renovado";
    private static final String FAILED_RENEWAL_RESULT_MSG = "Empréstimo não renovado";
    private static final String LAST_RENEWAL_RESULT_MSG = "Este empréstimo não poderá ser renovado novamente pelo terminal";

    private final Logger logger = Logger.getLogger(AcervusProxyRequests.class.getName());
    private CookieStore cookieStore = new BasicCookieStore();

    public AcervusProxyRequests() {
    }

    @Override
    public boolean login(User user) {

        if (this.user != null) {
            logout();
            this.user = null;
        }

        try (ClassicHttpResponse response = HttpUtils.doPost(API_LOGIN_URL, this.cookieStore, getLoginRequestJsonStringEntity(user))) {
            if (wasLoginSuccessful(response)) {
                this.user = user;
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.severe("[login()]: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void logout() {
        if (this.user == null) {
            return;
        }

        this.logger.info("[logout()] Logging out and clearing previous cookies...");
        this.cookieStore.clear();

        try (ClassicHttpResponse response = HttpUtils.doPost(API_LOGOUT_URL, this.cookieStore)) {
            if (response != null && response.getCode() != 302) {
                this.logger.warning("[Logout] HTTP status code is not 302 (received: " + response.getCode()
                        + "). Ignoring, as it seems there is no one logged in.");
            }
        } catch (IOException e) {
            logger.severe("[logout()]: " + e.getMessage());
        }
    }

    @Override
    public List<BorrowedBook> getBorrowedBooks() throws LoginException {
        if (!this.isLoggedIn()) {
            throw new LoginException();
        }
        LinkedList<BorrowedBook> borrowedBooks = new LinkedList<>();

        this.logger.info("[getBorrowedBooks()] Sending request...");
        request:
        try (ClassicHttpResponse response = HttpUtils.doPost(API_LIST_BORROWED_BOOKS_URL, this.cookieStore,
                new StringEntity("{\"sort\": \"DataEmprestimo-desc\"}"))) {

            if (response.getCode() != 200) {
                this.logger.severe(
                        "[getBorrowedBooks()] HTTP status code is not 200 (received: " + response.getCode() + "). Returning empty list.");
                break request;
            }

            JSONArray bookJsonArray = getBookJsonArray(response);
            return parseBookJsonArray(bookJsonArray);
        } catch (IOException | ParseException e) {
            logger.severe("[getBorrowedBooks()]: " + e.getMessage());
        }

        return borrowedBooks;
    }

    private boolean isLoggedIn() {
        try (ClassicHttpResponse response = HttpUtils.doGet(API_IS_LOGGED_IN_ULR, this.cookieStore)) {
            if (response != null && response.getCode() == 200) {
                boolean isLoggedIn = Boolean.parseBoolean(EntityUtils.toString(response.getEntity()));
                logger.info("[isLoggedIn()] IsLoggedIn: " + isLoggedIn);
                return isLoggedIn;
            } else {
                return false;
            }
        } catch (IOException | ParseException e) {
            logger.severe("[isLoggedIn()]: " + e.getMessage());
            return false;
        }
    }

    @Override
    public BookRenewalResult renewBooks(List<BorrowedBook> books) throws LoginException {
        if (!this.isLoggedIn()) {
            throw new LoginException();
        }

        BookRenewalResult renewalResult = new BookRenewalResult();

        if (books.isEmpty()) {
            this.logger.warning("[RenewBooks] No books to renew.");
            return renewalResult;
        }

        // Forge a renewal request to the Acervus API.
        JSONArray renewArray = buildRenewalRequestJson(books);
        request:
        try (ClassicHttpResponse response = HttpUtils.doPost(API_RENEWAL_URL, this.cookieStore,
                new StringEntity(renewArray.toString()))) {

            if (response.getCode() != 200) {
                this.logger.warning("[RenewBooks] HTTP status code is not 200 (received: " + response.getCode() + ")");
                break request;
            }

            HttpEntity entity = response.getEntity();
            JSONObject jsonResponse = new JSONObject(EntityUtils.toString(entity));
            JSONArray renewedBooks = jsonResponse.getJSONArray("CirculacaoRenovadaSet");
            JSONArray notRenewedBooks = jsonResponse.getJSONArray("CirculacaoNaoRenovadaSet");

            parseRenewedBooks(books, renewedBooks, renewalResult);
            parseNotRenewedBooks(books, notRenewedBooks, renewalResult);
        } catch (IOException | ParseException e) {
            logger.severe("[RenewBooks]: " + e.getMessage());
            e.printStackTrace();
        }

        return renewalResult;
    }

    @Override
    public BookRenewalResult renewBooksDueToday() throws LoginException {
        if (!this.isLoggedIn()) {
            throw new LoginException();
        }

        List<BorrowedBook> booksDueToday = new LinkedList<>();

        for (BorrowedBook book : this.getBorrowedBooks()) {
            if (book.getExpectedReturnDate().equals(LocalDate.now())) {
                booksDueToday.add(book);
            }
        }
        logger.info("[renewBooksDueToday()] booksDueToday: " + booksDueToday);

        return this.renewBooks(booksDueToday);
    }


    // Private interface:

    private StringEntity getLoginRequestJsonStringEntity(User user) {
        // Used by login()

        final JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("identificacao", user.getEmailDac());
        jsonPayload.put("senha", user.getSbuPassword());

        return new StringEntity(jsonPayload.toString());
    }

    private boolean wasLoginSuccessful(HttpResponse loginResponse) {
        // Used by login()

        if (loginResponse == null) {
            logger.severe("[Login] Login response is null");
            return false;
        }

        if (loginResponse.getCode() != 200) {
            this.logger.severe("[Login] HTTP status code is not 200 (received: " + loginResponse.getCode() + ")");
            return false;
        }

        this.logger.info("[Login] Login request was successfully accepted. Validating...");

        if (!this.isLoggedIn()) {
            this.logger.info("[Login] Login ultimately failed.");
            return false;
        }

        return true;
    }

    private JSONArray getBookJsonArray(ClassicHttpResponse bookResponse) throws IOException, ParseException {
        HttpEntity entity = bookResponse.getEntity();

        if (entity == null) {
            this.logger.severe("[GetBorrowedBooks] Response entity is null. Returning empty list.");
            return null;
        }

        String result = EntityUtils.toString(entity);
        JSONObject jsonObject = new JSONObject(result);
        if (!jsonObject.has("Result")) {
            this.logger.severe("[GetBorrowedBooks] JSON response does not contain 'Result' key. Returning empty list.");
            return null;
        }
        JSONObject resultObject = jsonObject.getJSONObject("Result");
        if (!resultObject.has("Data")) {
            this.logger.severe("[GetBorrowedBooks] JSON response does not contain 'Data' key. Returning empty list.");
            return null;
        }

        try {
            return resultObject.getJSONArray("Data");
        } catch (Exception e) {
            this.logger.severe(
                    "[GetBorrowedBooks] JSON response does not contain 'Data' key or it does not seem to be an array. Returning empty list.");
            return null;
        }
    }

    private List<BorrowedBook> parseBookJsonArray(JSONArray bookJsonArray) {
        List<BorrowedBook> borrowedBooks = new ArrayList<>();

        for (int i = 0; i < bookJsonArray.length(); i++) {
            JSONObject bookEntry = bookJsonArray.getJSONObject(i);
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

        return borrowedBooks;
    }

    private JSONArray buildRenewalRequestJson(List<BorrowedBook> books) {
        JSONArray renewArray = new JSONArray();
        for (BorrowedBook book : books) {
            JSONObject bookPayload = new JSONObject();
            bookPayload.put("Codigo", book.getCode());
            bookPayload.put("CodigoRegistro", book.getRegistryCode());
            bookPayload.put("Titulo", book.getTitle());
            renewArray.put(bookPayload);
        }

        return renewArray;
    }

    private void parseRenewedBooks(List<BorrowedBook> allBooks, JSONArray renewedBooks, BookRenewalResult renewalResult) {
        for (int i = 0; i < renewedBooks.length(); i++) {
            JSONObject book = renewedBooks.getJSONObject(i);

            this.logger.info("[RenewBooks] Book \"" + book.getString("Titulo") + "\" was successfully renewed.");
            BorrowedBook renewedBook = findBorrowedBookByTitle(allBooks, book.getString("Titulo"));
            renewedBook.setCanRenew(!book.getString("Resultado").contains(LAST_RENEWAL_RESULT_MSG));
            LocalDate newReturnDate = LocalDate.parse(book.getString("DataDevolucaoPrevista").split("T")[0]);
            renewedBook.setExpectedReturnDate(newReturnDate);
            renewalResult.getSuccessfullyRenewedBooks().add(renewedBook);
        }
    }

    private void parseNotRenewedBooks(List<BorrowedBook> allBooks, JSONArray notRenewedBooks, BookRenewalResult renewalResult) {
        for (int i = 0; i < notRenewedBooks.length(); i++) {
            JSONObject book = notRenewedBooks.getJSONObject(i);

            this.logger.info("[RenewBooks] Book \"" + book.getString("Titulo") + "\" wasn't renewed. Result = "
                    + book.getString("Resultado"));
            BorrowedBook notRenewedBook = findBorrowedBookByTitle(allBooks, book.getString("Titulo"));
            notRenewedBook.setCanRenew(false);
            renewalResult.getNotRenewedBooks().add(notRenewedBook);
        }
    }

    // This method exists because acervus' api is stupid and won't return the book's id code in its renewal results. Also,
    // it doesn't even return the book's full title, so partial name matching had to be implemented.
    private BorrowedBook findBorrowedBookByTitle(List<BorrowedBook> books, String title) {
        for (BorrowedBook book : books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }

        // If only part of the book's title was returned by the acervus api, look for partial matches:
        for (BorrowedBook book : books) {
            if (book.getTitle().contains(title)) {
                return book;
            }
        }

        return null;
    }
}
