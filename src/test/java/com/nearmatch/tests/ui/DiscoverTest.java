package com.nearmatch.tests.ui;

import com.microsoft.playwright.options.AriaRole;
import com.nearmatch.framework.ui.BaseUiTest;
import com.nearmatch.framework.ui.pom.pages.DiscoverPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class DiscoverTest extends BaseUiTest {

  /** Log in once before each test so we land on /discover. */
  @BeforeMethod
  public void login() {
    loginSeed();
  }

  @Test
  void discoverPageShowsStorySection() {
    DiscoverPage discover = new DiscoverPage(page);
    assertTrue(discover.storySectionTitle().isVisible());
  }

  @Test
  void discoverPageShowsFilterControls() {
    DiscoverPage discover = new DiscoverPage(page);
    // Radius select
    assertTrue(discover.radiusSelect().isVisible());
    // Age inputs
    assertTrue(discover.ageInputs().first().isVisible());
    // Filter button
    assertTrue(discover.filterButton().isVisible());
  }

  @Test
  void discoverPageShowsUserCards() {
    DiscoverPage discover = new DiscoverPage(page);
    // At least one user card with "Thả tim" button should be present
    discover.likeButtons().first().waitFor();
    assertTrue(discover.likeButtons().first().isVisible());
  }

  @Test
  void discoverPageShowsViewProfileLink() {
    DiscoverPage discover = new DiscoverPage(page);
    page.waitForSelector("a:has-text('Xem profile')");
    assertTrue(discover.viewProfileLinks().first().isVisible());
  }

  @Test
  void filterByRadiusUpdatesResults() {
    DiscoverPage discover = new DiscoverPage(page);
    discover.applyRadiusFilter("5");

    // Page should still be on /discover after filtering
    assertTrue(discover.url().contains("/discover"));
  }

  @Test
  void bottomNavIsVisible() {
    DiscoverPage discover = new DiscoverPage(page);
    assertTrue(discover.bottomNavLink("Khám phá").isVisible());
    assertTrue(discover.bottomNavLink("Matches").isVisible());
    assertTrue(discover.bottomNavLink("Chats").isVisible());
    assertTrue(discover.bottomNavLink("Story").isVisible());
  }

  @Test
  void likeButtonSendsRequest() {
    // Click the first "Thả tim" button and expect an alert (match or like confirmation)
    DiscoverPage discover = new DiscoverPage(page);
    discover.likeFirstUserAcceptDialog();

    // After dialog is dismissed, we should still be on discover
    assertTrue(discover.url().contains("/discover"));
  }
}
