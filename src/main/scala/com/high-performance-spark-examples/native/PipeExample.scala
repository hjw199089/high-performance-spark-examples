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
package com.highperformancespark.examples.ffi

import org.apache.spark.rdd._
import org.apache.spark.{SparkContext, SparkFiles}

object PipeExample {
  def lookupUserPRS(sc: SparkContext, input: RDD[Int]): RDD[(Int, List[String])] = {
    // Add file requires absolute paths
    val distScriptName = "ghinfo.pl"
    val localScript = System.getProperty("user.dir") + "/src/main/perl/" + distScriptName
    val addedFile = sc.addFile(localScript)

    // Pass enviroment variables to our worker
    val enviromentVars = Map("user" -> "apache", "repo" -> "spark")
    val result = input.map(x => x.toString).pipe(SparkFiles.get(distScriptName), enviromentVars)
    // Parse the results
    result.map{record =>
      val elems: Array[String] = record.split(" ")
      (elems(0).toInt, elems.slice(1, elems.size).sorted.distinct.toList)
    }
  }
}
