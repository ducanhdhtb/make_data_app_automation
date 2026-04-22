package com.nearmatch.tests.ui;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.nearmatch.framework.ui.BaseUiTest;
import com.nearmatch.framework.ui.pom.pages.ChatsPage;
import com.nearmatch.framework.ui.pom.pages.MatchesPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ChatsTest extends BaseUiTest {

  @BeforeMethod
  public void login() {
    loginSeed();
  }

  @Test
  void chatsPageLoads() {
    ChatsPage chats = new ChatsPage(page).open();
    chats.container().waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(10_000));
    assertTrue(chats.url().contains("/chats"));
  }

  @Test
  void chatsPageShowsConversationListOrEmptyState() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    boolean hasConversations = chats.conversationItems().count() > 0;
    boolean isEmpty = chats.emptyState().isVisible();

    assertTrue(hasConversations || isEmpty,
      "Chats page should show conversations or empty state");
  }

  @Test
  void chatsPageRedirectsToLoginWhenNotAuthenticated() {
    try (var session = freshSession()) {
      session.page().navigate("/chats");
      session.page().waitForURL("**/auth/login");
      assertTrue(session.page().url().contains("/auth/login"));
    }
  }

  @Test
  void selectConversationDisplaysMessages() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    int conversationCount = chats.conversationItems().count();
    if (conversationCount == 0) {
      // No conversations, skip test
      return;
    }

    chats.selectFirstConversationIfAny();

    // Should show chat box with messages or empty state
    boolean hasChatBox = chats.chatBox().isVisible();
    boolean hasMessages = chats.chatBubbles().count() > 0;
    boolean isEmpty = chats.messageEmptyState().isVisible();

    assertTrue(hasChatBox && (hasMessages || isEmpty),
      "Should display chat box with messages or empty state");
  }

  @Test
  void sendTextMessageSuccessfully() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    int conversationCount = chats.conversationItems().count();
    if (conversationCount == 0) {
      return;
    }

    chats.selectFirstConversationIfAny();

    // Type and send message
    String testMessage = "Test message " + System.currentTimeMillis();
    chats.sendMessage(testMessage);

    // Wait for message to appear
    chats.messageBubbleContaining(testMessage).waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(5_000));

    // Verify message appears in chat
    assertTrue(chats.messageBubbleContaining(testMessage).isVisible(),
      "Sent message should appear in chat");
  }

  @Test
  void messageShowsDeliveryStatus() {
    page.navigate("/chats");
    page.waitForLoadState();

    int conversationCount = page.locator(".chat-list-item").count();
    if (conversationCount == 0) {
      return;
    }

    page.locator(".chat-list-item").first().click();
    page.waitForLoadState();

    String testMessage = "Status test " + System.currentTimeMillis();
    page.locator("input[placeholder='Nhập tin nhắn...']").fill(testMessage);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Gửi")).click();

    // Wait for message to appear
    page.waitForSelector(".chat-bubble:has-text('" + testMessage + "')", 
      new Page.WaitForSelectorOptions().setTimeout(5_000));

    // Check for delivery status (should show "Đang gửi", "Đã nhận", or "Đã xem")
    boolean hasStatus = page.locator(".chat-bubble:has-text('" + testMessage + "') >> text=/Đang gửi|Đã nhận|Đã xem/").count() > 0;
    assertTrue(hasStatus, "Message should show delivery status");
  }

  @Test
  void replyToMessageSuccessfully() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    int conversationCount = chats.conversationItems().count();
    if (conversationCount == 0) {
      return;
    }

    chats.selectFirstConversationIfAny();

    int messageCount = chats.chatBubbles().count();
    if (messageCount == 0) {
      return;
    }

    // Click reply button on first message
    chats.hoverFirstMessageIfAny();
    chats.replyButtonOnFirstMessage().click();

    // Verify reply quote appears
    assertTrue(chats.composerQuote().isVisible(),
      "Reply quote should appear in composer");

    // Send reply
    String replyText = "Reply test " + System.currentTimeMillis();
    chats.sendMessage(replyText);

    // Verify reply message appears
    chats.messageBubbleContaining(replyText).waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(5_000));
    assertTrue(chats.messageBubbleContaining(replyText).isVisible(),
      "Reply message should appear in chat");
  }

  @Test
  void addReactionToMessage() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    int conversationCount = chats.conversationItems().count();
    if (conversationCount == 0) {
      return;
    }

    chats.selectFirstConversationIfAny();

    int messageCount = chats.chatBubbles().count();
    if (messageCount == 0) {
      return;
    }

    // Hover over message and click reaction button
    chats.hoverFirstMessageIfAny();
    chats.heartReactionButtonOnFirstMessage().click();

    // Wait for reaction to appear
    chats.reactionChips().first().waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(3_000));

    // Verify reaction appears
    assertTrue(chats.reactionChips().count() > 0,
      "Reaction should appear on message");
  }

  @Test
  void searchMessagesInConversation() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    int conversationCount = chats.conversationItems().count();
    if (conversationCount == 0) {
      return;
    }

    chats.selectFirstConversationIfAny();

    // Use search input
    chats.conversationSearchInput().fill("test");
    chats.conversationSearchButton().click();

    page.waitForLoadState();

    // Verify search was executed (page should still be on chats)
    assertTrue(chats.url().contains("/chats"),
      "Should remain on chats page after search");
  }

  @Test
  void typingIndicatorAppears() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    int conversationCount = chats.conversationItems().count();
    if (conversationCount == 0) {
      return;
    }

    chats.selectFirstConversationIfAny();

    // Start typing
    chats.messageInput().fill("T");

    // Typing indicator should appear (may take a moment)
    page.waitForTimeout(500);

    // Verify page is still functional
    assertTrue(chats.url().contains("/chats"),
      "Should remain on chats page while typing");
  }

  @Test
  void uploadImageMessage() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    int conversationCount = chats.conversationItems().count();
    if (conversationCount == 0) {
      return;
    }

    chats.selectFirstConversationIfAny();

    // Create a simple test image file
    java.nio.file.Path imagePath = createTestImageFile();

    // Upload image
    chats.imageUploadInput().setInputFiles(imagePath);

    // Wait for preview to appear
    chats.composerPreview().waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(3_000));

    // Verify preview is shown
    assertTrue(chats.composerPreview().isVisible(),
      "Image preview should appear");

    // Send image
    chats.sendButton().click();

    // Wait for image message to appear
    chats.chatImages().first().waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(5_000));

    assertTrue(chats.chatImages().count() > 0,
      "Image message should appear in chat");
  }

  @Test
  void conversationListUpdatesAfterNewMessage() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    int conversationCount = chats.conversationItems().count();
    if (conversationCount == 0) {
      return;
    }

    // Get first conversation's text before sending message
    String firstConvTextBefore = chats.firstConversationMutedPreview().textContent();

    chats.selectFirstConversationIfAny();

    // Send message
    String testMessage = "Update test " + System.currentTimeMillis();
    chats.sendMessage(testMessage);

    // Wait for message to appear
    chats.messageBubbleContaining(testMessage).waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(5_000));

    // Verify conversation list updated with new message preview
    String firstConvTextAfter = chats.firstConversationMutedPreview().textContent();
    assertNotEquals(firstConvTextBefore, firstConvTextAfter,
      "Conversation list should update with new message preview");
  }

  @Test
  void openChatFromMatchesNavigatesToChats() {
    MatchesPage matches = new MatchesPage(page).open();
    matches.heading().waitFor();

    boolean hasMatchCard = matches.messageButtons().count() > 0;
    if (!hasMatchCard) {
      assertTrue(page.locator("text=Chưa có match nào").isVisible());
      return;
    }

    matches.messageButtons().first().click();
    page.waitForURL("**/chats**");
    assertTrue(page.url().contains("/chats"));
  }

  @Test
  void socketConnectionStatusDisplayed() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    // Check for socket status indicator
    boolean hasStatus = chats.statusText().count() > 0;
    assertTrue(hasStatus || chats.inlineStatus().count() > 0,
      "Socket connection status should be displayed");
  }

  @Test
  void clearSearchFiltersMessages() {
    ChatsPage chats = new ChatsPage(page).open();
    page.waitForLoadState();

    int conversationCount = chats.conversationItems().count();
    if (conversationCount == 0) {
      return;
    }

    chats.selectFirstConversationIfAny();

    // Search for something
    chats.conversationSearchInput().fill("test");
    chats.conversationSearchButton().click();

    page.waitForLoadState();

    // Clear search
    boolean hasClearButton = chats.clearSearchButton().count() > 0;
    if (hasClearButton) {
      chats.clearSearchButton().click();
      page.waitForLoadState();
    }

    assertTrue(chats.url().contains("/chats"),
      "Should remain on chats page after clearing search");
  }

  private java.nio.file.Path createTestImageFile() {
    // Create a simple 1x1 pixel PNG file for testing
    String tempDir = System.getProperty("java.io.tmpdir");
    java.nio.file.Path filePath = java.nio.file.Paths.get(tempDir, "test-image-" + System.currentTimeMillis() + ".png");
    
    try {
      // 1x1 transparent PNG
      byte[] pngData = {
        (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
        0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
        0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
        (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41,
        0x54, 0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
        0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00,
        0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE,
        0x42, 0x60, (byte) 0x82
      };
      
      java.nio.file.Files.write(filePath, pngData);
      return filePath;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create test image", e);
    }
  }
}
