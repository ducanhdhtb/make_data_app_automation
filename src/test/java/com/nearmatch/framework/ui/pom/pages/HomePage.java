package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public final class HomePage extends BasePage {
  public HomePage(Page page) {
    super(page);
  }

  public HomePage open() {
    open("/");
    return this;
  }

  public Locator heroHeading() {
    return page.locator("h1:has-text('Tìm người phù hợp')");
  }

  public Locator featureCardTitle(String title) {
    return page.locator("h3:has-text('" + title + "')");
  }

  public Locator loginLink() {
    return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Đăng nhập để bắt đầu"));
  }

  public Locator registerLink() {
    return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Tạo tài khoản mới"));
  }

  public Locator brandLink() {
    return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("NearMatch")).first();
  }

  public LoginPage goToLogin() {
    loginLink().click();
    page.waitForURL("**/auth/login");
    return new LoginPage(page);
  }

  public RegisterPage goToRegister() {
    registerLink().click();
    page.waitForURL("**/auth/register");
    return new RegisterPage(page);
  }
}

