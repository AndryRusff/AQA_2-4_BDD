package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
  private final SelenideElement heading = $("[data-test-id=dashboard]");
  private final SelenideElement title = $("h1.heading");
  private final String balanceStart = "баланс:";
  private final String balanceFinish = "р.";

  public DashboardPage() {
    heading.shouldBe(visible);
    title.shouldHave(text("Ваши карты"));
  }

  private int extractBalance(String cardInfo) {
    var value = cardInfo.substring
            (cardInfo.indexOf(balanceStart) + balanceStart.length(),
                    cardInfo.indexOf(balanceFinish))
            .trim();
    return Integer.parseInt(value);
  }

  public int getCardBalance(DataHelper.CardId cardId) {
    return extractBalance($("[data-test-id='" + cardId.getId() + "']").getText());
  }

  public MoneyTransferPage transfer(DataHelper.CardId cardId) {
    $("[data-test-id='" + cardId.getId() + "'] [data-test-id=action-deposit]").click();
    return new MoneyTransferPage();
  }
}