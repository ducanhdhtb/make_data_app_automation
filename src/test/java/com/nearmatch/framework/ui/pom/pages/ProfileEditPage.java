package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public final class ProfileEditPage extends BasePage {
  public ProfileEditPage(Page page) {
    super(page);
  }

  public ProfileEditPage open() {
    open("/profile/edit");
    return this;
  }

  public Locator heading() {
    return page.locator("h1:has-text('Hồ sơ của tôi')");
  }

  public Locator displayNameInput() {
    return fieldInputByLabel("Tên hiển thị");
  }

  public Locator jobTitleInput() {
    return fieldInputByLabel("Nghề nghiệp");
  }

  public Locator cityInput() {
    return fieldInputByLabel("Thành phố");
  }

  public Locator bioTextarea() {
    return fieldTextareaByLabel("Bio");
  }

  public Locator interestsInput() {
    return fieldInputByLabel("Sở thích");
  }

  public Locator saveButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Lưu thay đổi"));
  }

  public Locator avatarFileInput() {
    return page.locator("input[type='file'][accept='image/*']");
  }

  public ProfileEditPage waitForProfileLoaded() {
    page.waitForFunction("() => !document.querySelector('p')?.textContent?.includes('Đang tải hồ sơ')");
    displayNameInput().waitFor();
    return this;
  }
}

