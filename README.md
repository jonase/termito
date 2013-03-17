# termito

Termito is a simple term rewriting library for clojure. It is inspired
by the [kibit](https://github.com/jonase/kibit) rule system but with
more features.

## Example usage

Here are some simple arithmetic rules:

```clojure
(defnc numberc [x] (number? x))

(defrules zero-rules
  [(* ?x 0) 0]
  [(* 0 ?x) 0])

(defrules identity-rules
  [(* ?x 1) ?x]
  [(* 1 ?x) ?x]
  [(+ ?x 0) ?x]
  [(+ 0 ?x) ?x])

(defrules constant-propagation-rules
  [(+ ?x ?y)
   :when [#{?x ?y} ~numberc]
   :with [?sum [?x ?y] ~+]
   ?sum]
  [(* ?x ?y)
   :when [#{?x ?y} ~numberc]
   :with [?prod [?x ?y] ~*]
   ?prod])

(def rules (concat zero-rules 
                   identity-rules 
                   constant-propagation-rules))

(simplify '(* (+ -1 2) (+ x 0)) rules)
;; => x
```

More examples can be found in [src/termito/examples](src/termito/examples).

## Usage

This library is not yet released to Clojars. If you really want to try
it you'll have to use core.logic master and not version 0.8.0.

## License

Copyright © 2013 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.
