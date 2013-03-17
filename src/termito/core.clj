(ns termito.core
  (:require [clojure.core.logic :as l :refer [defnc]]
            [clojure.core.logic.unifier :as u]
            [clojure.walk :as walk]
            [backtick :refer [template]]))

(defn rule 
  ([pat sbst] (rewrite-rule pat {} sbst))
  ([pat  opts sbst] 
     (fn [expr]
       (let [sym (gensym '?)
             match (u/unifier opts [pat expr])]
         (when match 
           (let [with (into {} (for [[ssym [psyms f]] (:with opts)]
                                 [ssym (apply f ((apply juxt psyms) match))]))
                 match-and-with (merge match with)
                 subst-clauses [(cons sym (keys match-and-with)) 
                                (cons sbst (vals match-and-with))]]
            (first (u/unify subst-clauses))))))))

;; simplify & simplify-one
;; TODO: what about nil?
(defn simplify-one [expr rules]
  (let [res (some (fn [rule]
                    (rule expr))
                  rules)]
    (if res res expr)))

(defn simplify [expr rules]
  (->> expr
       (iterate (partial walk/prewalk #(simplify-one % rules)))
       (partition 2 1)
       (drop-while #(apply not= %))
       (ffirst)))

;; defrules
(defn parse-with-clause [clause]
  (into {} (for [[ssym psyms f] (partition 3 clause)]
     [ssym [psyms f]])))

(defn parse-when-clause [clause]
  (apply hash-map clause))

(defn parse-rule [rule]
  (let [pat (first rule)
        sbst (last rule)
        opts (apply hash-map (-> rule rest drop-last))
        with-clause (when-let [with (:with opts)] 
                      (parse-with-clause with))
        when-clause (when-let [when (:when opts)] 
                      (parse-when-clause when))]
     [pat (merge {} 
                 (when with-clause {:with with-clause})
                 (when when-clause {:when when-clause})) 
      sbst]))

(defmacro defrules [name & rules]
  `(def ~name 
     (map (fn [r#]
            (apply rule (parse-rule r#)))
          (template ~rules))))

;; Example: simple arithmetic rules
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
   :when [#{?x y} ~numberc]
   :with [?sum [?x ?y] ~+]
   ?sum]
  [(* ?x ?y)
   :when [#{?x y} ~numberc]
   :with [?prod [?x ?y] ~*]
   ?prod])

(def rules (concat zero-rules identity-rules constant-propagation-rules))

(simplify '(* (+ -1 2) (+ x 0)) rules)
;; => x
