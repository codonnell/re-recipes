# re-recipes

A [re-frame](https://github.com/Day8/re-frame) application designed to display a list of Blue Apron recipes.

## Development Mode

### Run client application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Run server application:

```
lein repl
user=> (go)
```

Browse to [http://localhost:3000/index.html](http://localhost:3000/index.html).
Note that if you've run `lein clean` and the compiled javascript is not there, the client code will not run. In this case, simply run the client application in a separate terminal window.

### Run clojurescript tests:

```
lein clean
lein doo phantom test once
```

The above command assumes that you have [phantomjs](https://www.npmjs.com/package/phantomjs) installed. However, please note that [doo](https://github.com/bensu/doo) can be configured to run cljs.test in many other JS environments (chrome, ie, safari, opera, slimer, node, rhino, or nashorn). 

## Production Build

```
lein clean
lein cljsbuild once min
```
