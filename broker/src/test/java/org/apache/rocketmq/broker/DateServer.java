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

package org.apache.rocketmq.broker;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Set;
import java.util.Iterator;
import java.util.Date;
import java.nio.channels.ServerSocketChannel;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;

public class DateServer {
    public static void main(String args[]) throws Exception {
        // 通过open()方法找到Selector
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = null;
        // 打开服务器的通道
        serverSocketChannel = ServerSocketChannel.open();
        // 服务器配置为非阻塞
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        InetSocketAddress address = null;
        // 实例化绑定地址
        address = new InetSocketAddress(8000);
        // 进行服务的绑定
        serverSocket.bind(address);
        //监听客户端连接请求
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器运行，端口8000。");
        // 要接收全部生成的key，并通过连接进行判断是否获取客户端的输出
        int keysAdd = 0;
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        while (true) {
            while ((keysAdd = selector.select()) > 0) {    // 选择一组键，并且相应的通道已经准备就绪
                Set<SelectionKey> selectedKeys = selector.selectedKeys();// 取出全部生成的key
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();    // 取出每一个key
                    iter.remove();
                    try {
                        if (key.isValid()) {
                            //处理新接入的请求消息
                            if (key.isAcceptable()) {
                                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                                // 接收新连接
                                SocketChannel client = server.accept();
                                //设置为非阻塞的
                                client.configureBlocking(false);
                                buffer.clear();
                                buffer.put(new String(("isAcceptable当前的时间为：" + new Date()).getBytes(), "GBK").getBytes());
                                buffer.flip();
                                client.write(buffer);
                                //注册为读
                                client.register(selector, SelectionKey.OP_READ);
                            }
                            //读消息
                            else if (key.isReadable()) {
                                SocketChannel client = (SocketChannel) key.channel();
                                buffer.clear();
                                int readBytes = client.read(buffer);
                                //读取到字节，对字节进行编解码
                                if (readBytes > 0) {
                                    //根据缓冲区可读字节数创建字节数组
                                    byte[] bytes = new byte[buffer.remaining()];
                                    //将缓冲区可读字节数组复制到新建的数组中
                                    buffer.get(bytes);
                                    String value = new String(bytes, "UTF-8");
                                    System.out.println("服务器收到消息：" + value);
                                    //注册为写
                                    client.register(selector, SelectionKey.OP_WRITE);
                                } else if (readBytes < 0) {
                                    key.cancel();
                                    client.close();
                                }
                            }
                            //写消息
                            else if (key.isWritable()) {
                                SocketChannel client = (SocketChannel) key.channel();
                                buffer.clear();
                                buffer.put(new String(("isWritable当前的时间为：" + new Date()).getBytes(), "GBK").getBytes());
                                buffer.flip();
                                client.write(buffer);
                                //注册为读
                                client.register(selector, SelectionKey.OP_READ);
                            }
                            else if(key.isConnectable()){
                                System.out.println("isConnectable");
                            }
                        }
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }

                }
                selectedKeys.clear();    // 清楚全部的key
            }
        }

    }

}