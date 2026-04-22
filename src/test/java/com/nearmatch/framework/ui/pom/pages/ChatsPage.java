package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public final class ChatsPage extends BasePage {
  public ChatsPage(Page page) {
    super(page);
  }

  public ChatsPage open() {
    open("/chats");
    return this;
  }

  public Locator container() {
    return page.locator(".page, .container");
  }

  public Locator conversationItems() {
    return page.locator(".chat-list-item");
  }

  public Locator emptyState() {
    return page.locator("text=Chưa có hội thoại nào");
  }

  public Locator chatBox() {
    return page.locator(".chat-box");
  }

  public Locator chatBubbles() {
    return page.locator(".chat-bubble");
  }

  public Locator messageEmptyState() {
    return page.locator("text=Chưa có tin nhắn nào");
  }

  public Locator messageInput() {
    return page.locator("input[placeholder='Nhập tin nhắn...']");
  }

  public Locator sendButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Gửi"));
  }

  public Locator composerQuote() {
    return page.locator(".composer-quote");
  }

  public Locator reactionChips() {
    return page.locator(".reaction-chip");
  }

  public Locator conversationSearchInput() {
    return page.locator("input[placeholder='Tìm trong hội thoại...']");
  }

  public Locator conversationSearchButton() {
    return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Tìm"));
  }

  public Locator clearSearchButton() {
    return page.locator("button:has-text('Bỏ lọc')");
  }

  public Locator imageUploadInput() {
    return page.locator("input[type='file'][accept='image/*']");
  }

  public Locator composerPreview() {
    return page.locator(".composer-preview");
  }

  public Locator chatImages() {
    return page.locator(".chat-image");
  }

  public Locator inlineStatus() {
    return page.locator(".inline-status");
  }

  public Locator statusText() {
    return page.locator("text=/Trạng thái realtime|connected|disconnected/");
  }

  public ChatsPage selectFirstConversationIfAny() {
    if (conversationItems().count() == 0) return this;
    conversationItems().first().click();
    page.waitForLoadState();
    return this;
  }

  public ChatsPage hoverFirstMessageIfAny() {
    if (chatBubbles().count() == 0) return this;
    chatBubbles().first().hover();
    return this;
  }

  public Locator replyButtonOnFirstMessage() {
    return chatBubbles().first().locator("button:has-text('Reply')");
  }

  public Locator heartReactionButtonOnFirstMessage() {
    return chatBubbles().first().locator("button:has-text('❤️')");
  }

  public ChatsPage sendMessage(String message) {
    messageInput().fill(message);
    sendButton().click();
    return this;
  }

  public Locator messageBubbleContaining(String text) {
    return page.locator(".chat-bubble:has-text('" + text + "')");
  }

  public Locator firstConversationMutedPreview() {
    return conversationItems().first().locator(".muted");
  }
}
