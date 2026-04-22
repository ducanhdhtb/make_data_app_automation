package com.nearmatch.tests.ui;

import com.nearmatch.framework.ui.BaseUiTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class AuthRedirectTest extends BaseUiTest {
  @Test
  void discoverRedirectsToLoginWhenNotAuthenticated() {
    page.navigate("/discover");
    page.waitForURL("**/auth/login");
    assertTrue(page.url().contains("/auth/login"));
  }
}
