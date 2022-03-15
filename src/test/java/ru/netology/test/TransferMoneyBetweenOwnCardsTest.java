package ru.netology.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferMoneyBetweenOwnCardsTest {

    @BeforeAll
    public static void loginToPersonalAccount() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getUserAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor();
        verificationPage.validVerify(verificationCode);
    }

    @AfterEach
    public void cardBalancing() {
        var dashboardPage = new DashboardPage();
        var firstCardId = DataHelper.getFirstCardId();
        var balanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var secondCardId = DataHelper.getSecondCardId();
        var balanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        int amountTransfer;
        if (balanceFirstCard > balanceSecondCard) {
            amountTransfer = (balanceFirstCard - balanceSecondCard) / 2;
            var replenishmentPage = dashboardPage.transfer(secondCardId);
            var transferInfo = DataHelper.setSecondCardTransferInfo(amountTransfer);
            replenishmentPage.transferBetweenOwnCards(transferInfo);
        }
        if (balanceFirstCard < balanceSecondCard) {
            amountTransfer = (balanceSecondCard - balanceFirstCard) / 2;
            var replenishmentPage = dashboardPage.transfer(firstCardId);
            var transferInfo = DataHelper.setFirstCardTransferInfo(amountTransfer);
            replenishmentPage.transferBetweenOwnCards(transferInfo);
        }
    }

    @Test
    @DisplayName("Transfer money from sec card to first")
    public void shouldTransferFromSecondToFirst() {
        var dashboardPage = new DashboardPage();
        var firstCardId = DataHelper.getFirstCardId();
        var initialBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var secondCardId = DataHelper.getSecondCardId();
        var initialBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        var replenishmentPage = dashboardPage.transfer(firstCardId);
        var transferInfo = DataHelper.getFirstCardTransferInfoPositive();
        replenishmentPage.transferBetweenOwnCards(transferInfo);
        var finalBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var finalBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        assertEquals(transferInfo.getAmount(), finalBalanceFirstCard - initialBalanceFirstCard);
        assertEquals(transferInfo.getAmount(), initialBalanceSecondCard - finalBalanceSecondCard);
    }

    @Test
    @DisplayName("Transfer money from first card to sec")
    public void shouldTransferFromFirstToSecond() {
        var dashboardPage = new DashboardPage();
        var firstCardId = DataHelper.getFirstCardId();
        var initialBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var secondCardId = DataHelper.getSecondCardId();
        var initialBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        var replenishmentPage = dashboardPage.transfer(secondCardId);
        var transferInfo = DataHelper.getSecondCardTransferInfoPositive();
        replenishmentPage.transferBetweenOwnCards(transferInfo);
        var finalBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var finalBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        assertEquals(transferInfo.getAmount(), initialBalanceFirstCard - finalBalanceFirstCard);
        assertEquals(transferInfo.getAmount(), finalBalanceSecondCard - initialBalanceSecondCard);
    }

    @Test
    @DisplayName("Transfer money from sec card to first card negative amount")
    public void shouldTransferFromSecondToFirstNegativeAmount() {
        var dashboardPage = new DashboardPage();
        var firstCardId = DataHelper.getFirstCardId();
        var initialBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var secondCardId = DataHelper.getSecondCardId();
        var initialBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        var replenishmentPage = dashboardPage.transfer(firstCardId);
        var transferInfo = DataHelper.getFirstCardTransferInfoNegative();
        //Т.к. минус при вводе суммы игнорируется будет обычный перевод
        replenishmentPage.transferBetweenOwnCards(transferInfo);
        //Получение итогового баланса по обеим картам:
        var finalBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var finalBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        //Проверка зачисления на первую карту:
        assertEquals(-transferInfo.getAmount(), finalBalanceFirstCard - initialBalanceFirstCard);
        //Проверка списания со второй карты:
        assertEquals(-transferInfo.getAmount(), initialBalanceSecondCard - finalBalanceSecondCard);
    }

    @Test
    @DisplayName("Transfer money from first card to sec " +
            "with the transfer amount exceeding the balance of the first card")
    public void shouldTransferFromFirstToSecondNegativeAmount() {
        var dashboardPage = new DashboardPage();
        var firstCardId = DataHelper.getFirstCardId();
        var initialBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var secondCardId = DataHelper.getSecondCardId();
        var initialBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        var replenishmentPage = dashboardPage.transfer(secondCardId);
        var transferInfo = DataHelper.getSecondCardTransferInfoNegative();
        replenishmentPage.transferBetweenOwnCards(transferInfo);
        var finalBalanceFirstCard = dashboardPage.getCardBalance(firstCardId);
        var finalBalanceSecondCard = dashboardPage.getCardBalance(secondCardId);
        assertEquals(initialBalanceFirstCard, finalBalanceFirstCard,
                "Изменился баланс первой карты");
        assertEquals(initialBalanceSecondCard, finalBalanceSecondCard,
                "Изменился баланс второй карты");
    }
}