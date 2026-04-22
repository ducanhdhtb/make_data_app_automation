package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public final class LoginPage extends BasePage {
  public LoginPage(Page page) {
    super(page);
  }

  public LoginPage open() {
    open("/auth/login");
    return this;
  }

  public Locator emailInput() {
    return fieldInputByLabel("Email");
  }

  public Locator passwordInput() {
    return fieldPasswordByLabel("Mật khẩu");
  }

  public Locator submitButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Đăng nhập"));
  }

  public DiscoverPage loginAs(String email, String password) {
    emailInput().fill(email);
    passwordInput().fill(password);
    submitButton().click();
    page.waitForURL("**/discover");
    return new DiscoverPage(page);
  }
}

