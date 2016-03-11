# smogalert

### How to browse GAE APIs

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
