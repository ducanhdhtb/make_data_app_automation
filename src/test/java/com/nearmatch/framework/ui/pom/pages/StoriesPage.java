package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public final class StoriesPage extends BasePage {
  public StoriesPage(Page page) {
    super(page);
  }

  public StoriesPage open() {
    open("/stories");
    return this;
  }

  public Locator heading() {
    return page.locator("h1:has-text('Story')");
  }

  public Locator postFormHeading() {
    return page.locator("h2:has-text('Đăng story mới')");
  }

  public Locator storyTypeSelect() {
    return fieldSelectByLabel("Loại story");
  }

  public Locator postButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Đăng story"));
  }

  public Locator textContentTextarea() {
    return page.locator("textarea[placeholder='Hôm nay của bạn thế nào?']");
  }

  public Locator captionInput() {
    return page.locator("input[placeholder='Viết caption ngắn']");
  }

  public Locator fileInput() {
    return page.locator("input[type='file']");
  }

  public StoriesPage postTextStoryAcceptDialog(String text, String caption) {
    textContentTextarea().fill(text);
    captionInput().fill(caption);
    page.onDialog(dialog -> dialog.accept());
    postButton().click();
    return this;
  }
}

