(defproject clj-backend "0.1.0-SNAPSHOT"
  :description "A backend for Defy.ai"
  :url "https://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [compojure "1.7.0"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-defaults "0.4.0"]
                 [ring/ring-json "0.5.1"]
                 [cheshire "5.12.0"]
                 [http-kit "2.8.0"]
                 [hikari-cp "3.1.0"]
                 [org.postgresql/postgresql "42.7.0"]
                 [environ "1.2.0"]
                 [org.clojure/java.jdbc "0.7.12"]]
  :main ^:skip-aot clj-backend.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
