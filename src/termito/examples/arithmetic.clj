(ns termito.examples.arithmetic
  (:require [clojure.core.logic :refer [fnc defnc]]
            [termito.core :refer [defrules simplify]]))

(defnc numberc [x]
  (number? x))

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
   ?prod]
  [(- ?x ?y)
   :when [#{?x ?y} ~numberc]
   :with [?prod [?x ?y] ~-]
   ?prod]
  [(/ ?x ?y)
   :when [#{?x ?y} ~numberc]
   :with [?prod [?x ?y] ~/]
   ?prod])

(defrules associative-rules
  [(* ?a (* ?b ?c))
   :when [#{?a ?b} ~numberc]
   (* (* ?a ?b) ?c)]
  [(+ ?a (+ ?b ?c))
   :when [#{?a ?b} ~numberc]
   (+ (?a ?b) ?c)])

(defrules pow-rules
  [(pow ?x 1) ?x]
  [(pow ?x 0) 1]
)

(def rules
  (concat zero-rules
          identity-rules
          constant-propagation-rules
          associative-rules
          pow-rules))

(comment
  (simplify '(* 3 (* 5 a)) rules)
)