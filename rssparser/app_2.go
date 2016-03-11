package ghola

/*
This is a minimal sample application, demonstrating how to set up an RSS feed
for regular polling of new channels/items.

Build & run with:

 $ go run example.go

*/

import (
    "fmt"
    "net/http"
    "appengine"
    "appengine/datastore"
    "appengine/mail"
    "appengine/urlfetch"
)


func init() {
    http.HandleFunc("/", handler)
}

func handler(w http.ResponseWriter, r *http.Request) {
    fmt.Fprint(w, "Hello, world!")
    ctx := appengine.NewContext(r)
        client := urlfetch.Client(ctx)
        resp, err := client.Get("https://www.google.com/")
        if err != nil {
                http.Error(w, err.Error(), http.StatusInternalServerError)
                return
        }
        fmt.Fprintf(w, "HTTP GET returned status %v", resp.Status)
    //startPolling()
}

func startPolling() {
    // This sets up a new feed and polls it for new channels/items.
    // Invoking it with 'go PollFeed(...)' to have the polling performed in a
    // separate goroutine, so we can poll mutiple feeds.
    PollFeed("http://www.stateair.net/dos/RSS/HoChiMinhCity/HoChiMinhCity-PM2.5.xml", 5, nil)

    // Poll with a custom charset reader. This is to avoid the following error:
    // ... xml: encoding "ISO-8859-1" declared but Decoder.CharsetReader is nil.
    //PollFeed("https://status.rackspace.com/index/rss", 5, charsetReader)
}
