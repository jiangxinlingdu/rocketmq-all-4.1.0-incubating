/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.tools.command.message;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import org.apache.rocketmq.common.UtilAll;

public class Test {
    public static void main(String[] args) {

        String msgId = "AC1303C20010034B1D80986D3F547F09";
        try {

            byte[] ip = UtilAll.string2bytes(msgId.substring(0, 8));
            byte[] port = UtilAll.string2bytes(msgId.substring(8, 16));
            ByteBuffer bb = ByteBuffer.wrap(port);
            int portInt = bb.getInt(0);

            // offset
            byte[] data = UtilAll.string2bytes(msgId.substring(16, 32));
            bb = ByteBuffer.wrap(data);
            long offset = bb.getLong(0);

            System.out.println();
            System.out.printf(InetAddress.getByAddress(ip)+"======="+portInt+">>>>>>"+offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
