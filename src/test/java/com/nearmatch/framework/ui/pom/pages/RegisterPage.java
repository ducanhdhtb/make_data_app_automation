package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public final class RegisterPage extends BasePage {
  public RegisterPage(Page page) {
    super(page);
  }

  public RegisterPage open() {
    open("/auth/register");
    return this;
  }

  public Locator heading() {
    return page.locator("h1:has-text('Tạo tài khoản mới')");
  }

  public Locator emailInput() {
    return fieldInputByLabel("Email");
  }

  public Locator displayNameInput() {
    return fieldInputByLabel("Tên hiển thị");
  }

  public Locator passwordInput() {
    return fieldPasswordByLabel("Mật khẩu");
  }

  public Locator birthDateInput() {
    return page.locator(".field:has(label:has-text('Ngày sinh')) input[type='date']");
  }

  public Locator genderSelect() {
    return fieldSelectByLabel("Giới tính");
  }

  public Locator interestedInSelect() {
    return fieldSelectByLabel("Quan tâm tới");
  }

  public Locator submitButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Đăng ký"));
  }

  public LoginPage goToLoginLink() {
    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Đã có tài khoản")).click();
    page.waitForURL("**/auth/login");
    return new LoginPage(page);
  }

  public RegisterPage submit() {
    submitButton().click();
    return this;
  }
}

