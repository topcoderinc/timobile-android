package com.topcoder.timobile.baseclasses;

public class DebugBaseApplication extends BaseApplication {

  public void enableMockMode() {
    apiComponent = DaggerMockApiComponent.create();
  }
}
