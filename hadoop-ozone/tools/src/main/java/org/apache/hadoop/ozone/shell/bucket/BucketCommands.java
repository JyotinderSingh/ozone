/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.hadoop.ozone.shell.bucket;

import java.util.concurrent.Callable;

import org.apache.hadoop.hdds.cli.GenericParentCommand;
import org.apache.hadoop.hdds.cli.HddsVersionProvider;
import org.apache.hadoop.hdds.cli.MissingSubcommandException;
import org.apache.hadoop.hdds.cli.SubcommandWithParent;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.shell.OzoneShell;
import org.apache.hadoop.ozone.shell.Shell;

import org.kohsuke.MetaInfServices;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

/**
 * Subcommands for the bucket related operations.
 */
@Command(name = "bucket",
    description = "Bucket specific operations",
    subcommands = {
        InfoBucketHandler.class,
        ListBucketHandler.class,
        CreateBucketHandler.class,
        SetQuotaHandler.class,
        LinkBucketHandler.class,
        DeleteBucketHandler.class,
        AddAclBucketHandler.class,
        RemoveAclBucketHandler.class,
        GetAclBucketHandler.class,
        SetAclBucketHandler.class,
        ClearQuotaHandler.class,
        UpdateBucketHandler.class
    },
    mixinStandardHelpOptions = true,
    versionProvider = HddsVersionProvider.class)
@MetaInfServices(SubcommandWithParent.class)
public class BucketCommands implements GenericParentCommand, Callable<Void>,
    SubcommandWithParent {

  @ParentCommand
  private Shell shell;

  @Override
  public Void call() throws Exception {
    throw new MissingSubcommandException(
        this.shell.getCmd().getSubcommands().get("bucket"));
  }

  @Override
  public boolean isVerbose() {
    return shell.isVerbose();
  }

  @Override
  public OzoneConfiguration createOzoneConfiguration() {
    return shell.createOzoneConfiguration();
  }

  @Override
  public Class<?> getParentType() {
    return OzoneShell.class;
  }
}
