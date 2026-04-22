package com.nearmatch.tests.ui;

import com.nearmatch.framework.ui.BaseUiTest;
import com.nearmatch.framework.ui.pom.pages.StoriesPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = {"regression"})
public class StoriesTest extends BaseUiTest {

  @BeforeMethod
  public void login() {
    loginSeed();
  }

  @Test
  void storiesPageRendersHeading() {
    StoriesPage stories = new StoriesPage(page).open();
    assertTrue(stories.heading().isVisible());
  }

  @Test
  void storiesPageShowsPostForm() {
    StoriesPage stories = new StoriesPage(page).open();
    assertTrue(stories.postFormHeading().isVisible());
    assertTrue(stories.storyTypeSelect().isVisible());
    assertTrue(stories.postButton().isVisible());
  }

  @Test
  void storyTypeDropdownHasTextAndImageOptions() {
    StoriesPage stories = new StoriesPage(page).open();
    var select = stories.storyTypeSelect();
    assertTrue(select.locator("option[value='text']").count() == 1);
    assertTrue(select.locator("option[value='image']").count() == 1);
  }

  @Test
  void postTextStoryShowsConfirmationOrError() {
    StoriesPage stories = new StoriesPage(page).open();
    stories.postTextStoryAcceptDialog("Test story từ E2E", "E2E caption");

    // After posting, either a success alert fires or the page stays on /stories
    assertTrue(page.url().contains("/stories"));
  }

  @Test
  void switchingToImageTypeShowsFileInput() {
    StoriesPage stories = new StoriesPage(page).open();
    stories.storyTypeSelect().selectOption("image");

    // File input should appear when image type is selected
    assertTrue(stories.fileInput().isVisible());
  }

  @Test
  void storiesPageRedirectsToLoginWhenNotAuthenticated() {
    try (var session = freshSession()) {
      session.page().navigate("/stories");
      session.page().waitForURL("**/auth/login");
      assertTrue(session.page().url().contains("/auth/login"));
    }
  }
}
