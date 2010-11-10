(ns mmspec.reporting
  (:use
    [mmspec.exec :only (pass? fail?)]))

(defprotocol Reporter
  (report-pass [this])
  (report-fail [this])
  (report-runs [this results]))

(defn- print-failure [id result]
  (let [characteristic (.characteristic result)
        description @(.description characteristic)]
    (println)
    (println (str id ")"))
    (println (.name description) (.name characteristic) "FAILED")
    (println (.getMessage (.failure result)))))

(defn- print-failures [results]
  (println)
  (let [failures (filter fail? results)]
    (doseq [failure failures id (range 1 (inc (count failures)))]
      (print-failure id failure))))

(defn- tally-time [results]
  (loop [tally 0.0 results results]
    (if (seq results)
      (recur (+ tally (.seconds (first results))) (rest results))
      tally)))

(def seconds-format (java.text.DecimalFormat. "0.00000"))

(defn- print-duration [results]
  (println)
  (println "Finished in" (.format seconds-format (tally-time results)) "seconds"))

(defn- tally [results]
  (loop [results results all 0 fail 0]
    (if (not (seq results))
      {:all all :fail fail}
      (cond
        (fail? (first results)) (recur (rest results) (inc all) (inc fail))
        :else (recur (rest results) (inc all) fail)))))

(defn- print-tally [results]
  (println)
  (let [tally (tally results)]
    (print (:all tally) "examples," (:fail tally) "failures")))

(deftype ConsoleReporter []
  Reporter
  (report-pass [this]
    (print "."))
  (report-fail [this]
    (print "F"))
  (report-runs [this results]
    (print-failures results)
    (print-duration results)
    (print-tally results)))

(def *reporter* (ConsoleReporter.))

(defn active-reporter []
  *reporter*)

;..F.........
;
;1)
;'PrimeFactors should factor 3' FAILED
;expected: [2],
;     got: [3] (using ==)
;/Users/micahmartin/Projects/kata/prime_factors_kata/spec/prime_factors_spec.rb:22:
;
;Finished in 0.316175 seconds
;
;12 examples, 1 failure
