# termito

Termito is a simple term rewriting library for clojure. It is inspired
by the [kibit](https://github.com/jonase/kibit) rule system but with
more features.

## Usage

Add `[jonase/termito 0.0.1]` to your dependencies in project.clj.

## Example

Here are some simple arithmetic rules:

```clojure
(defrules zero-rules
  [(* 0 ?x) 0])

(defrules identity-rules
  [(* 1 ?x) ?x]
  [(+ 0 ?x) ?x])

(def rules (concat zero-rules 
                   identity-rules) 

(simplify '(+ (* 0 x) (* 1 y) rules)
;; => y
```

More advanced examples can be found in
[src/termito/examples](src/termito/examples). For example, combining
the [arithmetic rules](src/termito/examples/arithmetic.clj) with the
following derivative rules

```clojure
(defrules derivative-rules
  [(D (pow ?x ?n) ?x) (* ?n (pow ?x (- ?n 1)))]
  [(D (* ?a (pow ?x ?n)) ?x) (* ?a (D (pow ?x ?n) ?x))])
```

termito can solve simple math problems like "what is the second derivative of 5x^3":

```clojure
=> (simplify '(D (D (* 5 (pow x 3)) x) x) 
             (concat arithmetic/rules derivative-rules)))
(* 30 x)
``` 

## License

Copyright © 2013 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.
