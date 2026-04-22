package com.nearmatch.tests.ui;

import com.nearmatch.framework.ui.BaseUiTest;
import com.nearmatch.framework.ui.pom.pages.HomePage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = {"regression"})
public class HomePageTest extends BaseUiTest {

  @Test(groups = {"smoke"})
  void homePageRendersHeroHeading() {
    HomePage home = new HomePage(page()).open();
    assertTrue(home.heroHeading().isVisible());
  }

  @Test(groups = {"smoke"})
  void homePageHasLoginAndRegisterLinks() {
    HomePage home = new HomePage(page()).open();
    assertTrue(home.loginLink().isVisible());
    assertTrue(home.registerLink().isVisible());
  }

  @Test
  void homePageHasFeatureCards() {
    HomePage home = new HomePage(page()).open();
    assertTrue(home.featureCardTitle("Prisma + PostgreSQL").isVisible());
    assertTrue(home.featureCardTitle("JWT authentication").isVisible());
    assertTrue(home.featureCardTitle("Cloudinary upload").isVisible());
    assertTrue(home.featureCardTitle("Like và match").isVisible());
    assertTrue(home.featureCardTitle("Story 24 giờ").isVisible());
  }

  @Test
  void homePageLoginLinkNavigatesToLoginPage() {
    HomePage home = new HomePage(page()).open();
    home.goToLogin();
    assertTrue(home.url().contains("/auth/login"));
  }

  @Test
  void homePageRegisterLinkNavigatesToRegisterPage() {
    HomePage home = new HomePage(page()).open();
    home.goToRegister();
    assertTrue(home.url().contains("/auth/register"));
  }

  @Test
  void homePageTitleIsNearMatch() {
    new HomePage(page()).open();
    assertTrue(page().title().contains("NearMatch"));
  }

  @Test
  void navbarBrandLinkIsVisible() {
    HomePage home = new HomePage(page()).open();
    assertTrue(home.brandLink().isVisible());
  }
}
