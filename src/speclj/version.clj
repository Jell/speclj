(ns speclj.version
  (:require
    [clojure.string :as str]))

(def major 1)
(def minor 1)
(def tiny  0)
(def pre   nil)
(def string (str/join "." (filter identity [major minor tiny])))
(def summary (str "speclj " string))