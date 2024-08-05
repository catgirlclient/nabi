# Contributing
Contributions are always welcomed by the Nabi team.

## Notes
* We use Dragonfly as a drop in replacement for Redis, since it's more efficient and faster. If you'd like to replace it
  with something like [Cachegrand](https://github.com/danielealbano/cachegrand/tree/main) or 
  [Redis](https://github.com/redis/redis), feel free to do so.
* By default, the environment loaded will be ``DEVELOPMENT``. This means that Nabi will use the Discord Gateway API. If you
  want to test Nabi's webserver capabilities, please use ``DEVELOPMENT_WEBSERVER``.
* Please do not make any workarounds if the issue relates to Discord InteraKTions or Kord. It's recommended to open a 
  pull request related to the fix in the aforementioned repository.
