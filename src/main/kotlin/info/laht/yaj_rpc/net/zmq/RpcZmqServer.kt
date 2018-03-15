/*
 * The MIT License
 *
 * Copyright 2018 Lars Ivar Hatledal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package info.laht.yaj_rpc.net.zmq

import info.laht.yaj_rpc.RpcHandler
import info.laht.yaj_rpc.net.RpcServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zeromq.ZContext
import org.zeromq.ZMQ

open class RpcZmqServer(
        private val handler: RpcHandler
): RpcServer {

    override var port: Int? = null

    private var ctx: ZContext? = null
    private var socket: ZMQ.Socket? = null


    override fun start(port: Int) {

        if (socket == null) {

            this.port = port
            ctx = ZContext(1)
            socket = ctx!!.createSocket(ZMQ.REP).also {socket ->

                socket.bind("tcp://*:$port")

                Thread {

                    try {
                        while (true) {

                            val received = String(socket.recv(0), ZMQ.CHARSET)
                            LOG.trace(received)

                            handler.handle(received)?.also {
                                socket.send(it, 0)
                            } ?: socket.send("", 0)

                        }
                    } catch (ex: Exception) {
                        LOG.trace("Caught exception", ex)
                    }

                    LOG.info("${javaClass.simpleName} stopped!")

                }.start()

                LOG.info("${javaClass.simpleName} listening for connections on port: $port")

            }

        } else {
            LOG.warn("${javaClass.simpleName} is already running!")
        }

    }

    override fun stop() {
        socket?.apply {
            close()
            socket = null
        }
        ctx?.apply {
            close()
            ctx = null
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(RpcZmqServer::class.java)
    }

}