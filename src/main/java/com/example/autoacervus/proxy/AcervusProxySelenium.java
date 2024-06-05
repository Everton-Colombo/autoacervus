/*
 * package com.example.autoacervus.proxy;
 * 
 * import com.example.autoacervus.model.entity.BorrowedBook;
 * import com.example.autoacervus.model.BorrowedBookEntry;
 * import com.example.autoacervus.model.entity.User;
 * import com.example.autoacervus.util.SeleniumUtils;
 * import org.openqa.selenium.By;
 * import org.openqa.selenium.WebDriver;
 * import org.openqa.selenium.WebElement;
 * import org.openqa.selenium.support.ui.ExpectedCondition;
 * import org.openqa.selenium.support.ui.ExpectedConditions;
 * import org.openqa.selenium.support.ui.WebDriverWait;
 * 
 * import javax.security.auth.login.LoginException;
 * import java.time.Duration;
 * import java.time.LocalDate;
 * import java.time.format.DateTimeFormatter;
 * import java.util.LinkedList;
 * import java.util.List;
 * 
 * public class AcervusProxySelenium implements AcervusProxy {
 * private static final String homeUrl = "https://acervus.unicamp.br/";
 * private static final String emprestimoUrl =
 * "https://acervus.unicamp.br/emprestimo";
 * private static final String loggedOutRedirectUrl =
 * "https://acervus.unicamp.br/Login/Login";
 * 
 * private static final String failedLoginMsg = "Usuário ou senha inválida.";
 * 
 * private final WebDriver driver;
 * private final DateTimeFormatter dtFormatter =
 * DateTimeFormatter.ofPattern("dd/MM/yyyy");
 * 
 * public AcervusProxySelenium(WebDriver driver) {
 * this.driver = driver;
 * }
 * 
 * private User loggedInUser;
 * 
 * @Override
 * public boolean login(User user) {
 * loggedInUser = null;
 * 
 * SeleniumUtils.goToPage(driver, homeUrl);
 * 
 * WebElement signinBtn =
 * driver.findElement(By.cssSelector("a[title='Entrar']"));
 * signinBtn.click();
 * 
 * driver.switchTo().frame(driver.findElement(By.
 * cssSelector("#loginWindow > iframe")));
 * WebElement loginField = driver.findElement(By.name("identificacao"));
 * WebElement pwField = driver.findElement(By.name("senha"));
 * WebElement enterBtn =
 * driver.findElement(By.cssSelector("button[type='submit']"));
 * 
 * loginField.sendKeys(user.getEmailDac());
 * pwField.sendKeys(user.getSbuPassword());
 * enterBtn.click();
 * // Wait for login attempt results:
 * ExpectedCondition<Boolean> loginResultsAreShown = ExpectedConditions.or(
 * ExpectedConditions.invisibilityOfElementLocated(By.
 * cssSelector("#loginWindow > iframe")), // Successful
 * // login
 * ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(
 * "span.help-block"), failedLoginMsg) // Failed
 * // login
 * );
 * new WebDriverWait(driver,
 * Duration.ofSeconds(10)).until(loginResultsAreShown);
 * 
 * if (driver.getPageSource().contains(failedLoginMsg)) {
 * return false;
 * } else {
 * loggedInUser = user;
 * return true;
 * }
 * }
 * 
 * private void checkForLogin() throws LoginException {
 * if (driver.getCurrentUrl().equals(loggedOutRedirectUrl))
 * throw new LoginException("Cannot perform action: client not logged in.");
 * }
 * 
 * private BorrowedBook getBookFromRowData(List<WebElement> rowData) {
 * BorrowedBook borrowedBook = new BorrowedBook();
 * borrowedBook.setTitle(rowData.get(2).getText());
 * // borrowedBook.setCallNumber(rowData.get(3).getText());
 * // borrowedBook.setInventoryRegistryNumber(rowData.get(4).getText());
 * // borrowedBook.setLibrary(rowData.get(5).getText());
 * // LocalDate borrowDate = LocalDate.parse(rowData.get(6).getText(),
 * // dtFormatter);
 * // borrowedBook.setBorrowDate(borrowDate);
 * LocalDate expectedReturnDate = LocalDate.parse(rowData.get(7).getText(),
 * dtFormatter);
 * borrowedBook.setExpectedReturnDate(expectedReturnDate);
 * 
 * return borrowedBook;
 * }
 * 
 * private List<BorrowedBookEntry> getBorrowedBookEntries() throws
 * LoginException {
 * SeleniumUtils.goToPage(driver, emprestimoUrl);
 * 
 * checkForLogin();
 * List<WebElement> borrowedBooksRows = driver
 * .findElements(By.
 * cssSelector("div#gridCirculacaoAberta > table[role='grid'] > tbody > tr"));
 * 
 * List<BorrowedBookEntry> borrowedBookEntries = new LinkedList<>();
 * for (WebElement borrowedBookRow : borrowedBooksRows) {
 * List<WebElement> rowData =
 * borrowedBookRow.findElements(By.cssSelector("td"));
 * WebElement borrowedBookCheckbox =
 * rowData.get(0).findElement(By.cssSelector(".k-checkbox"));
 * 
 * BorrowedBookEntry borrowedBookEntry = new
 * BorrowedBookEntry(getBookFromRowData(rowData),
 * borrowedBookCheckbox);
 * borrowedBookEntries.add(borrowedBookEntry);
 * }
 * 
 * return borrowedBookEntries;
 * }
 * 
 * @Override
 * public List<BorrowedBook> getBorrowedBooks() throws LoginException {
 * List<BorrowedBook> borrowedBooks = new LinkedList<>();
 * for (BorrowedBookEntry entry : getBorrowedBookEntries()) {
 * entry.getBook().setBorrower(loggedInUser);
 * borrowedBooks.add(entry.getBook());
 * }
 * 
 * return borrowedBooks;
 * }
 * 
 * @Override
 * public boolean renewBook(BorrowedBook book) throws LoginException {
 * List<BorrowedBookEntry> entries = getBorrowedBookEntries();
 * for (BorrowedBookEntry entry : entries) {
 * if (entry.getBook().equals(book)) {
 * SeleniumUtils.clickWithJavascript(driver, entry.getCheckBox());
 * }
 * }
 * 
 * // TODO: check for fail condition
 * WebElement renewSelectedBtn = driver.findElement(By.id("btnRenovar"));
 * renewSelectedBtn.click();
 * return true;
 * }
 * 
 * @Override
 * public List<BorrowedBook> renewBooksDueToday() throws LoginException {
 * List<BorrowedBook> renewedBooks = new LinkedList<>();
 * for (BorrowedBookEntry entry : getBorrowedBookEntries()) {
 * if (entry.getBook().getExpectedReturnDate().equals(LocalDate.now())) {
 * renewedBooks.add(entry.getBook());
 * SeleniumUtils.clickWithJavascript(driver, entry.getCheckBox());
 * }
 * }
 * 
 * WebElement renewSelectedBtn = driver.findElement(By.id("btnRenovar"));
 * renewSelectedBtn.click();
 * 
 * return renewedBooks;
 * }
 * }
 */