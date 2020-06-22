package com.mng.rpc;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MainTest {

  ClientTelnetMain clientTelnetMain;

  @Before
  public void setup() {
    clientTelnetMain = new ClientTelnetMain();
  }

  @Test
  public void test() {
    assertTrue(clientTelnetMain != null);
  }
}
