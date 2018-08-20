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
	"github.com/urfave/cli"
	"crypto/tls"
	"crypto/x509"
	"encoding/binary"
	"fmt"
	"io/ioutil"
	"net"
	"os"
	"strconv"
	"time"
)

const (
	/* Authenticate message to check whether AccessKey is correct */
	TYPE_AUTH = 0x01

	/* There are no available ports for the access key */
	TYPE_NO_AVAILABLE_PORT = 0x02

	/* Holer connection message */
	TYPE_CONNECT = 0x03

	/* Holer disconnection message */
	TYPE_DISCONNECT = 0x04

	/* Holer data transfer */
	TYPE_TRANSFER = 0x05

	/* Access key is in use by other holer client */
	TYPE_IS_INUSE_KEY = 0x06

	/* Heart beat */
	TYPE_HEARTBEAT = 0x07

	/* Disabled access key */
	TYPE_DISABLED_ACCESS_KEY = 0x08

	/** Disabled trial client */
	TYPE_DISABLED_TRIAL_CLIENT = 0x09

	/** Invalid access key */
	TYPE_INVALID_KEY = 0x10

	/* Protocol field length */
	LEN_SIZE = 4

	TYPE_SIZE = 1

	URI_LEN_SIZE = 1

	SERIAL_NUM_SIZE = 8

	/* Heartbeat period */
	HEARTBEAT_INTERVAL = 30
)

type Message struct {
	Type      byte
	SerialNum uint64
	Uri       string
	Data      []byte
}

type HolerMsgHandler struct {
	Die         chan struct{}
	Pooler      *PoolHandler
	AccessKey   string
	ConnHandler *ConnHandler
}

type HolerConnPooler struct {
	Addr string
	Conf *tls.Config
}

func main() {
	fmt.Println("Welcome to Holer !")

	holer := cli.NewApp()
	holer.Name = "Holer"
	holer.Version = "1.0.0"
	holer.Usage = "exposes local servers behind NATs and firewalls to the public internet over secure tunnels."
	holer.Flags = []cli.Flag{
		cli.StringFlag{
			Name:  "k",
			Value: "",
			Usage: "Holer access key",
		},
		cli.StringFlag{
			Name:  "s",
			Value: "121.196.199.47",
			Usage: "Holer server host",
		},
		cli.IntFlag{
			Name:  "p",
			Value: 6060,
			Usage: "Holer server port",
		},
		cli.StringFlag{
			Name:  "ssl",
			Value: "false",
			Usage: "Enable SSL",
		},
		cli.StringFlag{
			Name:  "cer",
			Value: "",
			Usage: "SSL certificate path, default skip verify certificate",
		},
	}

	holer.Action = func(c *cli.Context) error {
		if c.String("k") == "" {
			fmt.Println("Holer access key is required, please use argument -k to specify it")
			fmt.Println("exit")
			return nil
		}

		if c.String("s") == "" {
			fmt.Println("Holer server host is required, please use argument -s to specify it")
			fmt.Println("exit")
			return nil
		}

		fmt.Println("Holer access key:", c.String("k"))

		var conf *tls.Config

		if c.String("ssl") == "true" {
			skipVerify := false
			cerPath := c.String("cer")

			if c.String("cer") == "" {
				skipVerify = true
				cerPath = "SSL certificate path is not specified, skip verify certificate"
			}

			conf = &tls.Config{
				InsecureSkipVerify: skipVerify,
			}

			fmt.Println("SSL certificate path:", cerPath)

			if c.String("cer") != "" {
				cert, err := ioutil.ReadFile(c.String("cer"))

				if err != nil {
					fmt.Println("Failed to load file", err)
					return nil
				}

				certPool := x509.NewCertPool()
				certPool.AppendCertsFromPEM(cert)
				conf.ClientCAs = certPool
			}
		}

		Start(c.String("k"), c.String("s"), c.Int("p"), conf)
		return nil
	}

	holer.Run(os.Args)
}

func Start(key string, host string, port int, conf *tls.Config) {
	pooler := &PoolHandler{Size: 100, Pool: &HolerConnPooler{Addr: host + ":" + strconv.Itoa(port), Conf: conf}}
	pooler.Init()

	connHandler := &ConnHandler{}

	for {
		conn := Connect(key, host, port, conf)
		connHandler.Conn = conn

		msgHandler := HolerMsgHandler{Pooler: pooler}
		msgHandler.ConnHandler = connHandler
		msgHandler.AccessKey = key
		msgHandler.HeartBeat()

		connHandler.Listen(conn, &msgHandler)
	}
}

func Connect(key string, host string, port int, conf *tls.Config) net.Conn {
	for {
		var conn net.Conn
		var err error

		p := strconv.Itoa(port)

		if conf != nil {
			conn, err = tls.Dial("tcp", host+":"+p, conf)
		} else {
			conn, err = net.Dial("tcp", host+":"+p)
		}

		if err != nil {
			fmt.Println("Error", err.Error())
			time.Sleep(time.Second * 20)
			continue
		}

		return conn
	}
}

func (msgHandler *HolerMsgHandler) Encode(msgData interface{}) []byte {
	if msgData == nil {
		return []byte{}
	}

	msg := msgData.(Message)

	snBytes := make([]byte, 8)
	binary.BigEndian.PutUint64(snBytes, msg.SerialNum)

	uriBytes := []byte(msg.Uri)
	bodyLen := TYPE_SIZE + SERIAL_NUM_SIZE + URI_LEN_SIZE + len(uriBytes) + len(msg.Data)

	data := make([]byte, LEN_SIZE, bodyLen+LEN_SIZE)
	binary.BigEndian.PutUint32(data, uint32(bodyLen))

	data = append(data, msg.Type)
	data = append(data, snBytes...)
	data = append(data, byte(len(uriBytes)))
	data = append(data, uriBytes...)
	data = append(data, msg.Data...)

	return data
}

func (msgHandler *HolerMsgHandler) Decode(buf []byte) (interface{}, int) {
	lenBytes := buf[0:LEN_SIZE]
	bodyLen := binary.BigEndian.Uint32(lenBytes)

	if uint32(len(buf)) < bodyLen+LEN_SIZE {
		return nil, 0
	}

	n := int(bodyLen + LEN_SIZE)
	body := buf[LEN_SIZE:n]

	msg := Message{}
	msg.Type = body[0]
	msg.SerialNum = binary.BigEndian.Uint64(body[TYPE_SIZE : SERIAL_NUM_SIZE+TYPE_SIZE])

	uriLen := uint8(body[SERIAL_NUM_SIZE+TYPE_SIZE])
	msg.Uri = string(body[SERIAL_NUM_SIZE+TYPE_SIZE+URI_LEN_SIZE : SERIAL_NUM_SIZE+TYPE_SIZE+URI_LEN_SIZE+uriLen])
	msg.Data = body[SERIAL_NUM_SIZE+TYPE_SIZE+URI_LEN_SIZE+uriLen:]

	return msg, n
}

func (msgHandler *HolerMsgHandler) Receive(connHandler *ConnHandler, msgData interface{}) {
	msg := msgData.(Message)

	switch msg.Type {
	case TYPE_CONNECT:
		go func() {
			//fmt.Println("Received connect message:", msg.Uri, "=>", string(msg.Data))
			intraServerHandler := &IntraServerMsgHandler{HolerConn: connHandler, Pooler: msgHandler.Pooler, Uri: msg.Uri, AccessKey: msgHandler.AccessKey}

			addr := string(msg.Data)
			conn, err := net.Dial("tcp", addr)

			if err != nil {
				fmt.Println("Failed to connect intranet server", err)
				intraServerHandler.Failure()
			} else {
				connHandler := &ConnHandler{}
				connHandler.Conn = conn
				connHandler.Listen(conn, intraServerHandler)
			}
		}()
	case TYPE_TRANSFER:
		if connHandler.NextConn != nil {
			connHandler.NextConn.Write(msg.Data)
		}
	case TYPE_DISCONNECT:
		if connHandler.NextConn != nil {
			connHandler.NextConn.Conn.Close()
			connHandler.NextConn.NextConn = nil
			connHandler.NextConn = nil
		}
		if msgHandler.AccessKey == "" {
			msgHandler.Pooler.Push(connHandler)
		}
	case TYPE_NO_AVAILABLE_PORT:
		fmt.Println("There are no available ports for the holer access key.")
		msgHandler.Close(connHandler)
		os.Exit(1)
	case TYPE_DISABLED_ACCESS_KEY:
		fmt.Println("Holer access key has been disabled.")
		msgHandler.Close(connHandler)
		os.Exit(1)
	case TYPE_INVALID_KEY:
		fmt.Println("Holer access key is not valid.")
		msgHandler.Close(connHandler)
		os.Exit(1)
	case TYPE_IS_INUSE_KEY:
		fmt.Println("Holer access key is in use by other holer client.")
		fmt.Println("If you want to have exclusive holer service")
		fmt.Println("please visit 'www.wdom.net' for more details.")
		msgHandler.Close(connHandler)
		os.Exit(1)
	case TYPE_DISABLED_TRIAL_CLIENT:
		fmt.Println("Your holer client is overuse.")
		fmt.Println("The trial holer access key can only be used for 20 minutes in 24 hours.")
		fmt.Println("If you want to have exclusive holer service")
		fmt.Println("please visit 'www.wdom.net' for more details.")
		msgHandler.Close(connHandler)
		os.Exit(1)
	}
}

func (msgHandler *HolerMsgHandler) HeartBeat() {
	msgHandler.Die = make(chan struct{})

	go func() {
		for {
			select {
			case <-time.After(time.Second * HEARTBEAT_INTERVAL):
				if time.Now().Unix()-msgHandler.ConnHandler.ReadTime >= 2*HEARTBEAT_INTERVAL {
					fmt.Println("Holer connection timeout")

					if msgHandler.ConnHandler != nil && msgHandler.ConnHandler.Conn != nil {
						msgHandler.ConnHandler.Conn.Close()
					}

					return
				}

				msg := Message{Type: TYPE_HEARTBEAT}
				msgHandler.ConnHandler.Write(msg)
			case <-msgHandler.Die:
				return
			}
		}
	}()
}

func (msgHandler *HolerMsgHandler) Close(connHandler *ConnHandler) {
	if msgHandler.Die != nil {
		close(msgHandler.Die)
	}

	if connHandler.NextConn != nil {
		connHandler.NextConn.NextConn = nil
		if connHandler.NextConn.Conn != nil {
			connHandler.NextConn.Conn.Close()
			connHandler.NextConn.Conn = nil
		}
		connHandler.NextConn = nil
	}

	if msgHandler.ConnHandler != nil && msgHandler.ConnHandler.Conn != nil {
		msgHandler.ConnHandler.Conn.Close()
		msgHandler.ConnHandler.Conn = nil
	}

	connHandler.MsgHandler = nil
	msgHandler.ConnHandler = nil
}

func (msgHandler *HolerMsgHandler) Success(connHandler *ConnHandler) {
	if msgHandler.AccessKey == "" {
		return
	}

	msg := Message{Type: TYPE_AUTH}
	msg.Uri = msgHandler.AccessKey
	connHandler.Write(msg)
}

func (msgHandler *HolerMsgHandler) Error(connHandler *ConnHandler) {
	//fmt.Println("Error:", connHandler)

	if msgHandler.Die != nil {
		close(msgHandler.Die)
	}

	if connHandler.NextConn != nil {
		connHandler.NextConn.NextConn = nil
		if connHandler.NextConn.Conn != nil {
			connHandler.NextConn.Conn.Close()
			connHandler.NextConn.Conn = nil
		}
		connHandler.NextConn = nil
	}

	connHandler.MsgHandler = nil
	msgHandler.ConnHandler = nil
	time.Sleep(time.Second * 3)
}

func (pooler *HolerConnPooler) Add(Pool *PoolHandler) (*ConnHandler, error) {
	var conn net.Conn
	var err error

	if pooler.Conf != nil {
		conn, err = tls.Dial("tcp", pooler.Addr, pooler.Conf)
	} else {
		conn, err = net.Dial("tcp", pooler.Addr)
	}

	if err != nil {
		fmt.Println("Error", err.Error())
		return nil, err
	}

	connHandler := &ConnHandler{}
	connHandler.Active = true
	connHandler.Conn = conn

	msgHandler := HolerMsgHandler{Pooler: Pool}
	connHandler.MsgHandler = interface{}(&msgHandler).(MsgHandler)
	msgHandler.ConnHandler = connHandler
	msgHandler.HeartBeat()

	go func() {
		connHandler.Listen(conn, &msgHandler)
	}()

	return connHandler, nil
}

func (pooler *HolerConnPooler) Delete(conn *ConnHandler) {
	conn.Conn.Close()
}

func (pooler *HolerConnPooler) IsActive(conn *ConnHandler) bool {
	return conn.Active
}
