# mahout-sample

This is my playground for using Mahout (the machine learning and
recommendation library) with Clojure.

## Data

An example `ratings.txt` is included in the `./data` directory. It
contains CSV records of: `userid,itemid,rating`.

## Usage

### Generating recommendations for a user

    user> (def r (user-recommender "./data/ratings.csv"))
    user> (user-recommendations r 1)
    ({:item 105, :value 4.0} {:item 104, :value 3.1406214})

    user> (estimate-user-preference r 1 104)
    3.1406214
    user> (estimate-user-preference r 1 107)
    NaN

## To Do:

* Include ns of fns to make it easier to validate models
* Build DataModel implementations that back onto Cassandra/MongoDB
* Build part-distributed/pre-computed and part-live recommendation system

## License

Copyright (c) 2011 Paul Ingles

Distributed under the Eclipse Public License, the same as Clojure.
