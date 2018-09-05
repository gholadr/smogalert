# smogalert

### How to browse the APIs locally

You'll need to run chrome as `unsafe mode`

In your bash profile, add the following alias
```
alias ogc='/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --user-data-dir=test --unsafely-treat-insecure-origin-as-secure=http://localhost:8080'
```
refresh your shell if need be `source ~/.bash_profile`, and start a new instance of chrome by running `ogc` in your shell

Now, Google's API Explorer will accept an unsafe `http` target:

```
https://apis-explorer.appspot.com/apis-explorer/?base=http%3A%2F%2Flocalhost%3A8080%2F_ah%2Fapi#p/
```

### Access the APIs on GAE

[SmogAlert APIs on GAE] (https://apis-explorer.appspot.com/apis-explorer/?base=https://smogalert-1248.appspot.com/_ah/api#p/aqi/v1/)

### Goodies under the hood

This app uses:

 [AirNow](https://docs.airnowapi.org/) to retrieve air pollution data from the US Embassy in HCMC.

 [Objectify](https://github.com/objectify/objectify) as a simple interface to GAE's datastore

 [Google Cloud Endpoints] (https://cloud.google.com/appengine/docs/java/endpoints/) to expose the APIs, and generate the client side libs
 
 [GreenRobot Eventbus] (https://github.com/greenrobot/EventBus) to manage simply app events
 
 [Iconify] (https://github.com/JoanZapata/android-iconify) for beautiful icons (only 1 so far)
 
 [Fabric] (https://fabric.io) Fabric.io for analytics
