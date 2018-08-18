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
	"fmt"
)

type IntraServerMsgHandler struct {
	Uri       string
	AccessKey string
	Pooler    *PoolHandler
	HolerConn *ConnHandler
}

func (msgHandler *IntraServerMsgHandler) Encode(msg interface{}) []byte {
	if msg == nil {
		return []byte{}
	}
	return msg.([]byte)
}

func (msgHandler *IntraServerMsgHandler) Decode(buf []byte) (interface{}, int) {
	return buf, len(buf)
}

func (msgHandler *IntraServerMsgHandler) Receive(connHandler *ConnHandler, msgData interface{}) {
	if connHandler.NextConn == nil {
		return
	}

	data := msgData.([]byte)
	msg := Message{Type: TYPE_TRANSFER}
	msg.Data = data

	connHandler.NextConn.Write(msg)
}

func (msgHandler *IntraServerMsgHandler) Success(connHandler *ConnHandler) {
	holerHandler, err := msgHandler.Pooler.Pull()
	if err != nil {
		fmt.Println("Get holer connection error:", err, "Uri:", msgHandler.Uri)

		msg := Message{Type: TYPE_DISCONNECT}
		msg.Uri = msgHandler.Uri

		msgHandler.HolerConn.Write(msg)
		connHandler.Conn.Close()
	} else {
		holerHandler.NextConn = connHandler
		connHandler.NextConn = holerHandler

		msg := Message{Type: TYPE_CONNECT}
		msg.Uri = msgHandler.Uri + "@" + msgHandler.AccessKey

		holerHandler.Write(msg)
		//fmt.Println("Intranet server connect success, notify holer server:", message.Uri)
	}
}

func (msgHandler *IntraServerMsgHandler) Error(connHandler *ConnHandler) {
	conn := connHandler.NextConn

	if conn != nil {
		msg := Message{Type: TYPE_DISCONNECT}
		msg.Uri = msgHandler.Uri

		conn.Write(msg)
		conn.NextConn = nil
	}

	connHandler.MsgHandler = nil
}

func (msgHandler *IntraServerMsgHandler) Failure() {
	msg := Message{Type: TYPE_DISCONNECT}
	msg.Uri = msgHandler.Uri

	msgHandler.HolerConn.Write(msg)
}
