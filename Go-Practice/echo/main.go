package main

import (
	"flag"
	"fmt"
	gnet "github.com/panjf2000/gnet/v2"
	"log"
)

type echoServer struct {
	gnet.BuiltinEventEngine

	eng       gnet.Engine
	addr      string
	multicore bool
}

func (es *echoServer) OnOpen(c gnet.Conn) (out []byte, action gnet.Action) {
	log.Printf("client connected. address:%s", c.RemoteAddr().String())
	return nil, gnet.None
}

func (es *echoServer) OnClose(c gnet.Conn, err error) (action gnet.Action) {
	log.Printf("client disconnected. address:%s, error:%v", c.RemoteAddr().String(), err)
	return gnet.None
}

func (es *echoServer) onBoot(eng gnet.Engine) gnet.Action {
	es.eng = eng
	log.Printf("echo server with multi-core=%t is listening on %s\n", es.multicore, es.addr)
	return gnet.None
}

func (es *echoServer) OnTraffic(c gnet.Conn) gnet.Action {
	buf, _ := c.Next(-1) // Read all available data from the connection
	c.Write(buf)
	return gnet.None
}

func main() {
	var port int
	var multicore bool

	flag.IntVar(&port, "port", 9000, "--port 9000")
	flag.BoolVar(&multicore, "multicore", false, "--multicore true")
	flag.Parse()
	echo := &echoServer{
		addr:      fmt.Sprintf("tcp://:%d", port),
		multicore: multicore,
	}
	log.Fatal(gnet.Run(echo, echo.addr, gnet.WithMulticore(multicore)))
}
