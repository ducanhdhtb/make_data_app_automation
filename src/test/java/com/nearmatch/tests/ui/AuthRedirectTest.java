package com.nearmatch.tests.ui;

import com.nearmatch.framework.ui.BaseUiTest;
import com.nearmatch.framework.ui.pom.pages.DiscoverPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = {"regression"})
public class AuthRedirectTest extends BaseUiTest {
  @Test(groups = {"smoke"})
  void discoverRedirectsToLoginWhenNotAuthenticated() {
    new DiscoverPage(page()).open();
    page().waitForURL("**/auth/login");
    assertTrue(page().url().contains("/auth/login"));
  }
}
