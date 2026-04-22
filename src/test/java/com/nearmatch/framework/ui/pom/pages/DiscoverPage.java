package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public final class DiscoverPage extends BasePage {
  public DiscoverPage(Page page) {
    super(page);
  }

  public DiscoverPage open() {
    open("/discover");
    return this;
  }

  public Locator storySectionTitle() {
    return page.locator("h3:has-text('Story đang hoạt động')");
  }

  public Locator radiusSelect() {
    return page.locator("select").first();
  }

  public Locator ageInputs() {
    return page.locator("input[type='number']");
  }

  public Locator filterButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Lọc"));
  }

  public Locator likeButtons() {
    return page.locator("button:has-text('Thả tim')");
  }

  public Locator viewProfileLinks() {
    return page.locator("a:has-text('Xem profile')");
  }

  public Locator bottomNavLink(String name) {
    return page.locator("a:has-text('" + name + "')").last();
  }

  public DiscoverPage applyRadiusFilter(String radiusKmValue) {
    likeButtons().first().waitFor();
    radiusSelect().selectOption(radiusKmValue);
    filterButton().click();
    return this;
  }

  public DiscoverPage likeFirstUserAcceptDialog() {
    likeButtons().first().waitFor();
    page.onDialog(dialog -> dialog.accept());
    likeButtons().first().click();
    return this;
  }
}
