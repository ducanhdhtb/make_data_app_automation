package com.nearmatch.framework.junit;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

public final class TestOutcome implements AfterTestExecutionCallback {
  private volatile Throwable error;

  @Override
  public void afterTestExecution(ExtensionContext context) {
    this.error = context.getExecutionException().orElse(null);
  }

  public boolean failed() {
    return error != null;
  }

  public Optional<Throwable> error() {
    return Optional.ofNullable(error);
  }
}

