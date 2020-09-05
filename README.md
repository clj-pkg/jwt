# Clojure JWT ![Clojure CI](https://github.com/clj-pkg/jwt/workflows/Clojure%20CI/badge.svg) [![Clojars Project](https://img.shields.io/clojars/v/clj-pkg/jwt.svg)](https://clojars.org/clj-pkg/jwt)

This is a minimal implementation of JWT on Clojure inspired by Go version [robbert229/jwt](https://github.com/robbert229/jwt).

## What is JWT?
JSON Web Token (JWT) is a compact URL-safe means of representing claims to be transferred between two parties. Learn more [jwt.io](https://jwt.io/).

## What algorithms does it support?
* HS256

## How does it work?

### Installation

Include in your project.clj

![](https://clojars.org/clj-pkg/jwt/latest-version.svg)

```clojure
(require [clj-pkg.jwt :as jwt])
```

### How to create a token?
Creating a token is actually pretty easy.

```clojure
(jwt/sign (jwt/jwt :hs256 {:a "1" :b "2"}) "ThisIsTheSecret")
```
    
### How to validate a token?

```clojure
(jwt/verify (jwt/str->jwt valid-jwt) "ThisIsTheSecret")
```
