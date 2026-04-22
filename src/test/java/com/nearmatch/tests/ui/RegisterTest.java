package com.nearmatch.tests.ui;

import com.nearmatch.framework.ui.BaseUiTest;
import com.nearmatch.framework.ui.pom.pages.RegisterPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = {"regression"})
public class RegisterTest extends BaseUiTest {

  @Test
  void registerPageRendersAllFields() {
    RegisterPage register = new RegisterPage(page).open();

    assertTrue(register.heading().isVisible());
    assertTrue(register.emailInput().isVisible());
    assertTrue(register.displayNameInput().isVisible());
    assertTrue(register.passwordInput().isVisible());
    assertTrue(register.birthDateInput().isVisible());
    assertTrue(register.genderSelect().isVisible());
    assertTrue(register.interestedInSelect().isVisible());
    assertTrue(register.submitButton().isVisible());
  }

  @Test
  void registerPageHasLinkBackToLogin() {
    RegisterPage register = new RegisterPage(page).open();
    register.goToLoginLink();
    assertTrue(register.url().contains("/auth/login"));
  }

  @Test
  void registerWithDuplicateEmailShowsError() {
    RegisterPage register = new RegisterPage(page).open();

    register.emailInput().fill("linh@example.com");
    register.displayNameInput().fill("Test User");
    register.passwordInput().fill("Password123!");
    register.birthDateInput().fill("1995-06-15");
    register.submit();

    // Should stay on register page and show an error
    page.waitForSelector("p[style*='color']");
    assertTrue(page.url().contains("/auth/register"));
  }

  @Test
  void genderDropdownHasExpectedOptions() {
    RegisterPage register = new RegisterPage(page).open();
    var genderSelect = register.genderSelect();
    assertTrue(genderSelect.locator("option[value='female']").count() == 1);
    assertTrue(genderSelect.locator("option[value='male']").count() == 1);
    assertTrue(genderSelect.locator("option[value='other']").count() == 1);
  }
}
