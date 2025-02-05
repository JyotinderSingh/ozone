/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm.server;

import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * This class is used to test the StorageContainerManagerStarter using a mock
 * class to avoid starting any services and hence just test the CLI component.
 */
public class TestStorageContainerManagerStarter {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();

  private MockSCMStarter mock;

  @Before
  public void setUpStreams() throws UnsupportedEncodingException {
    System.setOut(new PrintStream(outContent, false, DEFAULT_ENCODING));
    System.setErr(new PrintStream(errContent, false, DEFAULT_ENCODING));
    mock = new MockSCMStarter();
  }

  @After
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  public void testCallsStartWhenServerStarted() throws Exception {
    executeCommand();
    assertTrue(mock.startCalled);
  }

  @Test
  public void testExceptionThrownWhenStartFails() throws Exception {
    mock.throwOnStart = true;
    try {
      executeCommand();
      fail("Exception show have been thrown");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testStartNotCalledWithInvalidParam() throws Exception {
    executeCommand("--invalid");
    assertFalse(mock.startCalled);
  }

  @Test
  public void testPassingInitSwitchCallsInit() {
    executeCommand("--init");
    assertTrue(mock.initCalled);
  }

  @Test
  public void testPassingBootStrapSwitchCallsBootStrap() {
    executeCommand("--bootstrap");
    assertTrue(mock.bootStrapCalled);
  }

  @Test
  public void testInitSwitchAcceptsClusterIdSSwitch() {
    executeCommand("--init", "--clusterid=abcdefg");
    assertEquals("abcdefg", mock.clusterId);
  }

  @Test
  public void testInitSwitchWithInvalidParamDoesNotRun() {
    executeCommand("--init", "--clusterid=abcdefg", "--invalid");
    assertFalse(mock.initCalled);
  }

  @Test
  public void testBootStrapSwitchWithInvalidParamDoesNotRun() {
    executeCommand("--bootstrap", "--clusterid=abcdefg", "--invalid");
    assertFalse(mock.bootStrapCalled);
  }

  @Test
  public void testUnSuccessfulInitThrowsException() {
    mock.throwOnInit = true;
    try {
      executeCommand("--init");
      fail("Exception show have been thrown");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testUnSuccessfulBootStrapThrowsException() {
    mock.throwOnBootstrap = true;
    try {
      executeCommand("--bootstrap");
      fail("Exception show have been thrown");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testGenClusterIdRunsGenerate() {
    executeCommand("--genclusterid");
    assertTrue(mock.generateCalled);
  }

  @Test
  public void testGenClusterIdWithInvalidParamDoesNotRun() {
    executeCommand("--genclusterid", "--invalid");
    assertFalse(mock.generateCalled);
  }

  @Test
  public void testUsagePrintedOnInvalidInput()
      throws UnsupportedEncodingException {
    executeCommand("--invalid");
    Pattern p = Pattern.compile("^Unknown option:.*--invalid.*\nUsage");
    Matcher m = p.matcher(errContent.toString(DEFAULT_ENCODING));
    assertTrue(m.find());
  }

  private void executeCommand(String... args) {
    new StorageContainerManagerStarter(mock).execute(args);
  }

  static class MockSCMStarter implements SCMStarterInterface {

    private boolean initStatus = true;
    private boolean throwOnStart = false;
    private boolean throwOnInit  = false;
    private boolean throwOnBootstrap  = false;
    private boolean startCalled = false;
    private boolean initCalled = false;
    private boolean bootStrapCalled = false;
    private boolean generateCalled = false;
    private String clusterId = null;

    @Override
    public void start(OzoneConfiguration conf) throws Exception {
      if (throwOnStart) {
        throw new Exception("Simulated error on start");
      }
      startCalled = true;
    }

    @Override
    public boolean init(OzoneConfiguration conf, String cid)
        throws IOException {
      if (throwOnInit) {
        throw new IOException("Simulated error on init");
      }
      initCalled = true;
      clusterId = cid;
      return initStatus;
    }

    @Override
    public boolean bootStrap(OzoneConfiguration conf)
        throws IOException {
      if (throwOnBootstrap) {
        throw new IOException("Simulated error on init");
      }
      bootStrapCalled = true;
      return initStatus;
    }

    @Override
    public String generateClusterId() {
      generateCalled = true;
      return "static-cluster-id";
    }
  }
}