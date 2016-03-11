package main

/*
This is a minimal sample application, demonstrating how to set up an RSS feed
for regular polling of new channels/items.

Build & run with:

 $ go run example.go

*/

import (
    "errors"
    "fmt"
    "io"
    "os"
    "time"
    "strings"
    "encoding/json"
    rss "github.com/jteeuwen/go-pkg-rss"
    "github.com/jteeuwen/go-pkg-xmlx"
)


func main() {
    // This sets up a new feed and polls it for new channels/items.
    // Invoking it with 'go PollFeed(...)' to have the polling performed in a
    // separate goroutine, so we can poll mutiple feeds.
    PollFeed("http://www.stateair.net/dos/RSS/HoChiMinhCity/HoChiMinhCity-PM2.5.xml", 5, nil)

    // Poll with a custom charset reader. This is to avoid the following error:
    // ... xml: encoding "ISO-8859-1" declared but Decoder.CharsetReader is nil.
    //PollFeed("https://status.rackspace.com/index/rss", 5, charsetReader)
}

func PollFeed(uri string, timeout int, cr xmlx.CharsetFunc) {
    feed := rss.New(timeout, true, chanHandler, itemHandler)
    for {
        if err := feed.Fetch(uri, cr); err != nil {
            fmt.Fprintf(os.Stderr, "[e] %s: %s\n", uri, err)
            return
        }

       <-time.After(time.Duration(feed.SecondsTillUpdate() * 1e9))
    }
}

func chanHandler(feed *rss.Feed, newchannels []*rss.Channel) {
   // fmt.Printf("%d new channel(s) in %s\n", len(newchannels), feed.Url)
}

func itemHandler(feed *rss.Feed, ch *rss.Channel, newitems []*rss.Item) {
    //fmt.Printf("%d new item(s) in %s\n", len(newitems), feed.Url)

    layout := "2006-01-02 15:04:05"
    last_sample := newitems[len(newitems)-1]
   // for k, val := range newitems { 
    aqi_data :=strings.Split(last_sample.Description, ";")
    t, _ := time.Parse(layout, aqi_data[0])
    fmt.Printf("value %s\n", t)
    bolB, _ := json.Marshal(aqi_data)
    fmt.Println(string(bolB))
    //updateEtc(aqi_data, bolB)
}

func charsetReader(charset string, r io.Reader) (io.Reader, error) {
    if charset == "ISO-8859-1" || charset == "iso-8859-1" {
        return r, nil
    }
    return nil, errors.New("Unsupported character set encoding: " + charset)
}

// func updateEtc(sample []string, jsonStr []byte){
//    //url := "http://127.0.0.1:2379/v2/keys/samples"
//        cfg := client.Config{
//         Endpoints:               []string{"http://127.0.0.1:2379"},
//         Transport:               client.DefaultTransport,
//         // set timeout per request to fail fast when the target endpoint is unavailable
//         HeaderTimeoutPerRequest: time.Second,
//     }
//     c, err := client.New(cfg)
//     if err != nil {
//         log.Fatal(err)
//     }
//     kapi := client.NewKeysAPI(c)
//     // set "/foo" key with "bar" value
//     log.Print("Setting '/samples' key with 'jsonStr' value")
//     resp, err := kapi.Set(context.Background(), "/samples", jsonStr, nil)
//     if err != nil {
//         log.Fatal(err)
//     } else {
//         // print common key info
//         log.Printf("Set is done. Metadata is %q\n", resp)
//     }
//     // get "/foo" key's value
//     log.Print("Getting '/samples' key value")
//     resp, err = kapi.Get(context.Background(), "/samples", nil)
//     if err != nil {
//         log.Fatal(err)
//     } else {
//         // print common key info
//         log.Printf("Get is done. Metadata is %q\n", resp)
//         // print value
//         log.Printf("%q key has %q value\n", resp.Node.Key, resp.Node.Value)
//     }
   //  fmt.Println("URL:>", url)

   //  req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonStr))
   // // req.Header.Set("X-Custom-Header", "myvalue")
   // // req.Header.Set("Content-Type", "application/json")

   //  client := &http.Client{}
   //  resp, err := client.Do(req)
   //  if err != nil {
   //      panic(err)
   //  }
   //  defer resp.Body.Close()

   //  fmt.Println("response Status:", resp.Status)
   //  fmt.Println("response Headers:", resp.Header)
   //  body, _ := ioutil.ReadAll(resp.Body)
   //  fmt.Println("response Body:", string(body))

//}