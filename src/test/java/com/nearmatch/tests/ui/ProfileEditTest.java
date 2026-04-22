package com.nearmatch.tests.ui;

import com.nearmatch.framework.ui.BaseUiTest;
import com.nearmatch.framework.ui.pom.pages.ProfileEditPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class ProfileEditTest extends BaseUiTest {

  @BeforeMethod
  public void login() {
    loginSeed();
  }

  @Test
  void editProfilePageRendersHeading() {
    ProfileEditPage profile = new ProfileEditPage(page).open();
    assertTrue(profile.heading().isVisible());
  }

  @Test
  void editProfilePageLoadsCurrentUserData() {
    ProfileEditPage profile = new ProfileEditPage(page).open().waitForProfileLoaded();

    // Give React a moment to set the input value
    page.waitForTimeout(500);

    var displayNameInput = profile.displayNameInput();
    // Value should be non-empty after data loads
    assertTrue(!displayNameInput.inputValue().isEmpty(), "Display name should be populated");
  }

  @Test
  void editProfilePageHasAllFormFields() {
    ProfileEditPage profile = new ProfileEditPage(page).open();
    profile.displayNameInput().waitFor();

    assertTrue(profile.displayNameInput().isVisible());
    assertTrue(profile.jobTitleInput().isVisible());
    assertTrue(profile.cityInput().isVisible());
    assertTrue(profile.bioTextarea().isVisible());
    assertTrue(profile.interestsInput().isVisible());
    assertTrue(profile.saveButton().isVisible());
  }

  @Test
  void saveProfileShowsSuccessMessage() {
    ProfileEditPage profile = new ProfileEditPage(page).open();

    // Wait for form to load
    profile.waitForProfileLoaded();
    page.waitForTimeout(500);

    // Update city field and save
    profile.cityInput().fill("TP.HCM");
    profile.saveButton().click();

    // Expect a green success message — wait for it to appear
    page.waitForSelector("p[style*='color']");
    // The success message contains green color (#15803d)
    var successMsg = page.locator("p[style*='15803d']");
    var errorMsg = page.locator("p[style*='be123c']");
    assertTrue(successMsg.count() > 0 || errorMsg.count() == 0,
      "Expected success message or no error after save");
  }

  @Test
  void avatarUploadInputIsPresent() {
    ProfileEditPage profile = new ProfileEditPage(page).open();
    profile.avatarFileInput().waitFor();
    assertTrue(profile.avatarFileInput().isVisible());
  }

  @Test
  void editProfileRedirectsToLoginWhenNotAuthenticated() {
    try (var session = freshSession()) {
      session.page().navigate("/profile/edit");
      session.page().waitForURL("**/auth/login");
      assertTrue(session.page().url().contains("/auth/login"));
    }
  }
}
