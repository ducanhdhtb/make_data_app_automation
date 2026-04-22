package com.nearmatch.tests.ui;

import com.nearmatch.framework.ui.BaseUiTest;
import com.nearmatch.framework.ui.pom.pages.DiscoverPage;
import com.nearmatch.framework.ui.pom.pages.LoginPage;
import com.nearmatch.framework.config.TestConfig;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class LoginTest extends BaseUiTest {
  @Test
  void loginWithSeedAccountNavigatesToDiscover() {
    DiscoverPage discover = new LoginPage(page)
      .open()
      .loginAs(TestConfig.seedEmail(), TestConfig.seedPassword());

    assertTrue(discover.url().contains("/discover"));
    assertTrue(discover.storySectionTitle().first().isVisible());
  }
}
