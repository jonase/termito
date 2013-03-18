(ns termito.examples.arithmetic
  (:require [clojure.core.logic :refer [fnc defnc]]
            [termito.core :refer [defrules simplify]]))

(defnc numberc [x]
  (number? x))

(defnc commutative? [op]
  (or (= op '*)
      (= op '+)))

(defnc associative? [op]
  (or (= op '*)
      (= op '+)))

(defrules zero-rules
  [(* 0 ?x) 0])

(defrules identity-rules
  [(* 1 ?x) ?x]
  [(+ 0 ?x) ?x])

(defrules associative-rules
  [(?op ?x (?op ?y ?z))
   :when [?op ~associative?
          #{?x ?y} ~numberc]
   (?op (?op ?x ?y) ?z)])

(defrules commutative-rules
  [(?op ?x ?y)
   :when [?op ~commutative?
          [?x ?y] ~(fnc [x y] 
                     (and (number? y)
                          (not (number? x))))]
   (?op ?y ?x)])

(defrules constant-propagation-rules
  [(?op ?x ?y)
   :when [#{?x ?y} ~numberc]
   :with [?sum [?op ?x ?y] ~#((resolve %1) %2 %3)]
   ?sum])

(defrules pow-rules
  [(pow ?x 1) ?x]
  [(pow ?x 0) 1])

(def rules
  (concat zero-rules
          identity-rules
          constant-propagation-rules
          associative-rules
          commutative-rules
          pow-rules))

(comment
  (simplify '(+ (* 1 x) 0) rules)
  (simplify '(* 3 (* 5 a)) rules)
)