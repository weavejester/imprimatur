# Imprimatur

*Let it be printed*

A ClojureScript library for visualizing a data structure as a
[React][] component.

It's designed primarily to work with [Brutha][], but should also be
usable from other React-based libraries.

[react]:  https://facebook.github.io/react/
[brutha]: https://github.com/weavejester/brutha

## Installation

To install, add the following to your project `:dependencies`:

    [imprimatur "0.1.1"]

## Usage

Imprimatur provides a `print` function that returns a component
visualizing a data structure.

```clojure
(ns example.core
  (:require [brutha.core :as br]
            [imprimatur.core :as imp]))

(def main (.getElementById js/document "main"))

(br/mount (imp/print {:root "Hello World"}) main)
```

This will display the `"Hello World"` string in the element with an id
of `main`.

You can also visualize collections:

```clojure
(br/mount (imp/print {:root {:foo "bar"}}))
```

However, you may notice that in this case, the collection is displayed
like:

```
{...}
```

This is because collections are closed by default. To open the
collection we need to set its visibility:


```clojure
(imp/print {:root {:foo "bar"}, :visibility {}})
```

Visibility is defined as a data structure. If the result of:

```clojure
(get-in visibility keys)
```

Is truthy, then the collection indexed by `keys` will be open. If not,
it will be closed. For example:

```clojure
(imp/print {:root {:a {:b {:c :d} :e {:f :g}}}
            :visibility {:a {:b {}}}})
```

This opens the data structure at `:a` and `:b`, but not at `:e`. The
visualization will resemble:

```
{:a {:b {:c :d} :e {...}}}
```

Another way of generating the visibility data structure is to use the
`open` and `close` functions:

```
(-> nil (imp/open [:a :b]))
=> {:a {:b {}}}
```


To allow the end user to change the visibility, you'll need to set an
`:on-toggle` handler. This function is called when the user clicks one
of the toggle buttons. You can use it to change the visibility.

For example:

```clojure
(def data {:a {:b {:c :d} :e {:f :g}}})

(def visibility (atom nil))

(defn render []
  (br/mount
   (imp/print
    {:root       data
     :visibility @visibility
     :on-toggle  #(swap! visibility imp/toggle %)})
   main))

(add-watch visibility :change (fn [_ _ _ _] (render)))

(render)
```

The `toggle` function will `open` a collection if it's closed, or
`close` a collection if it's open.

Look in the `example` directory of the repository for a more detailed
example.

## License

Copyright © 2016 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
