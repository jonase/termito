(ns termito.example.derivatives
  (:require [clojure.core.logic :refer [fnc defnc]]
            [termito.core :refer [defrules simplify]]
            [termito.examples.arithmetic :as arithmetic]))

(defrules derivative-rules
  [(D (pow ?x ?n) ?x) (* ?n (pow ?x (- ?n 1)))]
  [(D (* ?a (pow ?x ?n)) ?x) (* ?a (D (pow ?x ?n) ?x))])

(def all-rules (concat derivative-rules
                       arithmetic/rules))

;; What's the second derivative of 5x^3
(simplify '(D (D (* 5 (pow x 3)) x) x) all-rules)
;; => (* 30 x)