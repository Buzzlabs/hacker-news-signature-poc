(use-modules
  (gnu packages clojure)
  (gnu packages java)
  (guix packages)
  (guix git)
  (guix git-download)
  (guix build-system clojure)
  ((guix licenses) #:prefix license:))

(define clojure-hato
  (package
    (name "clojure-hato")
    (version "1.0.0")
    (home-page "https://github.com/gnarroway/hato")
    (source
     (origin
       (method git-fetch)
       (uri (git-reference
             (url home-page)
             (commit (string-append "v" version))))
       (file-name (git-file-name name version))
       (sha256
        (base32 "0ab3cvai41p9z333krp0dcqvbf856ag28xkss3cg9izfavd6vpxn"))))
    (build-system clojure-build-system)
    (arguments
     (list
      #:jdk openjdk17 ; JDK 11 or newer is required
      #:tests? #f
      #:doc-dirs `(list)))
    (synopsis "HTTP client for Clojure, wrapping JDK 11's HttpClient")
    (description
     "An HTTP client for Clojure, wrapping JDK 11's HttpClient.

It supports both HTTP/1.1 and HTTP/2, with synchronous and asynchronous execution modes as well as websockets.

In general, it will feel familiar to users of http clients like clj-http. The API is designed to be idiomatic and to make common tasks convenient, whilst still allowing the underlying HttpClient to be configured via native Java objects.")
    (license license:expat)))

(define hacker-news-signature-poc
  (package
    (name "hacker-news-signature-poc")
    (version "0.0.0")
    (home-page "https://contaaberta.info")
    (source (git-checkout (url (dirname (current-filename)))))
    (build-system clojure-build-system)
    (propagated-inputs (list clojure-hato clojure-data-json))
    (arguments
     (list
      #:jdk openjdk17 ; JDK 11 or newer is required
      #:tests? #f
      #:doc-dirs `(list)))
    (synopsis "")
    (description "")
    (license license:expat))) ; TODO: fix the license

hacker-news-signature-poc
