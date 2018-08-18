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
	"sync"
)

type PoolHandler struct {
	Size  int
	Mu    sync.Mutex
	Pool  HolerPool
	Conns []*ConnHandler
}

type HolerPool interface {
	Add(pooler *PoolHandler) (*ConnHandler, error)
	Delete(conn *ConnHandler)
	IsActive(conn *ConnHandler) bool
}

func (pooler *PoolHandler) Init() {
	pooler.Conns = make([]*ConnHandler, 0, pooler.Size)
}

func (pooler *PoolHandler) Pull() (*ConnHandler, error) {
	for {
		if len(pooler.Conns) == 0 {
			conn, err := pooler.Pool.Add(pooler)
			if err != nil {
				return nil, err
			}
			return conn, nil
		} else {
			conn, err := pooler.GetConn()
			if conn != nil {
				return conn, err
			}
		}
	}
}

func (pooler *PoolHandler) Push(conn *ConnHandler) {
	pooler.Mu.Lock()
	defer pooler.Mu.Unlock()

	if len(pooler.Conns) >= pooler.Size {
		pooler.Pool.Delete(conn)
	} else {
		pooler.Conns = pooler.Conns[:len(pooler.Conns)+1]
		pooler.Conns[len(pooler.Conns)-1] = conn
	}
}

func (pooler *PoolHandler) GetConn() (*ConnHandler, error) {
	pooler.Mu.Lock()
	defer pooler.Mu.Unlock()

	if len(pooler.Conns) == 0 {
		return nil, nil
	}

	conn := pooler.Conns[len(pooler.Conns)-1]
	pooler.Conns = pooler.Conns[:len(pooler.Conns)-1]

	if pooler.Pool.IsActive(conn) {
		return conn, nil
	} else {
		fmt.Println("There is no holer connection.")
		return nil, nil
	}
}
