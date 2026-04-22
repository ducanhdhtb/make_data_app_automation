package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public final class NotificationsPage extends BasePage {
  public NotificationsPage(Page page) {
    super(page);
  }

  public NotificationsPage open() {
    open("/notifications");
    return this;
  }

  public Locator heading() {
    return page.locator("h1:has-text('Thông báo')");
  }

  public Locator reloadButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Tải lại"));
  }

  public Locator markReadButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Đánh dấu đã đọc"));
  }

  public Locator items() {
    return page.locator(".mini-item");
  }

  public Locator emptyState() {
    return page.locator("text=Bạn chưa có thông báo nào");
  }

  public NotificationsPage markAllReadAcceptDialog() {
    page.onDialog(dialog -> dialog.accept());
    markReadButton().click();
    return this;
  }
}

