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
    "net/http"
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
    a := append(aqi_data[:1], aqi_data[3:]...)
    bolB, _ := json.Marshal(a)
    fmt.Println(string(bolB))
    fmt.Println(strings.Join(a,"/"))
    APIURL :=  "http://localhost:8080/_ah/api/qirqualitysampleapi/v1/airqualitysample/75/moderate/2016-03-11%2010%3A00%20PM" // "http://localhost:8080/_ah/api/qirqualitysampleapi/v1/airqualitysample/" + strings.Join(a,"/")

    req, err := http.NewRequest("POST", APIURL, nil)
    fmt.Println(req)
    fmt.Println(err)
}

func charsetReader(charset string, r io.Reader) (io.Reader, error) {
    if charset == "ISO-8859-1" || charset == "iso-8859-1" {
        return r, nil
    }
    return nil, errors.New("Unsupported character set encoding: " + charset)
}


