/*
 * Copyright 2018-present, Yudong (Dom) Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package main

import (
	//"fmt"
	"net"
	//"runtime/debug"
	"time"
)

type ConnHandler struct {
	ReadTime   int64
	WriteTime  int64
	Active     bool
	ReadBuf    []byte
	Conn       net.Conn
	NextConn   *ConnHandler
	MsgHandler MsgHandler
}

type MsgHandler interface {
	Success(connHandler *ConnHandler)
	Error(connHandler *ConnHandler)
	Encode(msgData interface{}) []byte
	Decode(buf []byte) (interface{}, int)
	Receive(connHandler *ConnHandler, msgData interface{})
}

func (connHandler *ConnHandler) Write(msg interface{}) {
	if connHandler.MsgHandler == nil {
		return
	}
	data := connHandler.MsgHandler.Encode(msg)
	connHandler.WriteTime = time.Now().Unix()
	connHandler.Conn.Write(data)
}

func (connHandler *ConnHandler) Listen(conn net.Conn, msgHandler interface{}) {
	defer func() {
		if err := recover(); err != nil {
			//fmt.Printf("Warn: %v", err)
			//debug.PrintStack()
			connHandler.MsgHandler.Error(connHandler)
		}
	}()

	if conn == nil {
		return
	}

	connHandler.Conn = conn
	connHandler.Active = true
	connHandler.ReadTime = time.Now().Unix()
	connHandler.WriteTime = connHandler.ReadTime
	connHandler.MsgHandler = msgHandler.(MsgHandler)
	connHandler.MsgHandler.Success(connHandler)

	for {
		buf := make([]byte, 1024*8)

		// A packet cannot be more than 2M in size
		if connHandler.ReadBuf != nil && len(connHandler.ReadBuf) > 2*1024*1024 {
			connHandler.Conn.Close()
		}

		n, err := connHandler.Conn.Read(buf)
		if err != nil || n == 0 {
			connHandler.Active = false
			connHandler.MsgHandler.Error(connHandler)
			break
		}

		connHandler.ReadTime = time.Now().Unix()
		if connHandler.ReadBuf == nil {
			connHandler.ReadBuf = buf[0:n]
		} else {
			connHandler.ReadBuf = append(connHandler.ReadBuf, buf[0:n]...)
		}

		for {
			msg, n := connHandler.MsgHandler.Decode(connHandler.ReadBuf)
			if msg == nil {
				break
			}

			connHandler.MsgHandler.Receive(connHandler, msg)
			connHandler.ReadBuf = connHandler.ReadBuf[n:]
			if len(connHandler.ReadBuf) == 0 {
				break
			}
		}

		if len(connHandler.ReadBuf) > 0 {
			buf := make([]byte, len(connHandler.ReadBuf))
			copy(buf, connHandler.ReadBuf)
			connHandler.ReadBuf = buf
		}
	}
}
