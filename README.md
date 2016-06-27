Dirty playground
================

I created this repository to play a bit with a couple of some new awesome Java tools and
mix them with my existing favourite library stack and concepts. I focus on things and
concepts that I'm interested in. As the stuff is intentionally DIRTY, I simplify some
stuff that is not currently too important, to get the actual feedback as quickly as
possible.

Now, what's used:
 * Guice - my favourite DI container,
 * Guava - source of excellent collections and their immutable counterparts,
 * Vert.x Web - NIO-based HTTP core,
 * RxJava - reactive programming stuff.
 * MongoDB - I don't have too many practices with NoSQL databases, so I want to find
   some nice use cases for them.

Concepts being tested:
 * CQRS,
 * some event sourcing,
 * loose coupling between the two above and the model,

What is going to be included soon:
 * Gradle - looks promising,
 * Keycloak - leave the security to the security guys.

Main rules:
 * there is no one, true way,
 * follow the rules - see what happens,
 * break the rules - see what happens,
 * invent various crazy design problems and try to solve them,
 * FIF - Failing Is Fun; if something doesn't work, delete it and start again with something different,
 * DRYF - Don't Repeat Your Fails,
 * RYS - Repeat Your Successes ;).

I learn much more by problem solving rather than reading tutorials. Making basic fails on production
code sometimes is not desirable. You risk freezing your fails forever, loss of respect by the guys that
will have to maintain the code in the future, etc. So I decided to create a dirty playground... to play :).

The source code is shared under Apache License 2.0.
