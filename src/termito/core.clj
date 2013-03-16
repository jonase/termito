(ns termito.core
  (:require [clojure.core.logic :as l]
            [clojure.core.logic.unifier :as u]))

(defn rewrite-rule 
  ([pat sbst] (rewrite-rule pat sbst {}))
  ([pat sbst opts] 
     (fn [expr]
       (let [sym (gensym '?)]
         (sym (u/unifier opts
                       [[sym pat]
                        [sbst expr]]))))))

(def inc-rule 
  (rewrite-rule '(+ ?x 1) '(inc ?x)))

(inc-rule '(+ 10 1))
; => (inc 10)