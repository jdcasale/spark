/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.shuffle.sort.lifecycle;

import com.google.common.collect.ImmutableMap;
import org.apache.spark.SparkEnv;
import org.apache.spark.api.shuffle.ShuffleDriverComponents;
import org.apache.spark.internal.config.package$;
import org.apache.spark.storage.BlockManagerMaster;

import java.io.IOException;
import java.util.Map;

public class DefaultShuffleDriverComponents implements ShuffleDriverComponents {

  private BlockManagerMaster blockManagerMaster;
  private boolean shouldUnregisterOutputOnHostOnFetchFailure;

  @Override
  public Map<String, String> initializeApplication() {
    blockManagerMaster = SparkEnv.get().blockManager().master();
    this.shouldUnregisterOutputOnHostOnFetchFailure =
        SparkEnv.get().blockManager().externalShuffleServiceEnabled()
            && (boolean) SparkEnv.get().conf()
            .get(package$.MODULE$.UNREGISTER_OUTPUT_ON_HOST_ON_FETCH_FAILURE());
    return ImmutableMap.of();
  }

  @Override
  public void cleanupApplication() {
    // do nothing
  }

  @Override
  public void removeShuffleData(int shuffleId, boolean blocking) throws IOException {
    checkInitialized();
    blockManagerMaster.removeShuffle(shuffleId, blocking);
  }

  @Override
  public boolean shouldUnregisterOutputOnHostOnFetchFailure() {
    return shouldUnregisterOutputOnHostOnFetchFailure;
  }

  private void checkInitialized() {
    if (blockManagerMaster == null) {
      throw new IllegalStateException("Driver components must be initialized before using");
    }
  }
}
