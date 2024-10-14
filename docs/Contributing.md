# Contributing
Contributions are always welcomed by the Nabi team, as we strive to be both free and open source.

## Building and Running Nabi
> [!WARNING]
> Nabi wasn't made for the intention of being self-hosted, as it's typically taylored for larger servers and higher load 
> operations. Due to this, you won't recieve support beyond this guide on how to set up Nabi and run her related services.

Nabi requires the following dependencies:

* Java 17 (Discord InteraKTions require it for webserver based interactions.)
* Redis/Any Redis compatible in memory database (Required by [`subprojects/cache`](../subprojects/cache))
* PostgreSQL (Required by [`subprojects/database`](../subprojects/database), though you can swap this out.)
* An HTTP server (*Optional*, although if you want other users to access Nabi's servers and websites, you'll need to specify this. 
I personally recommend Caddy or Nginx for reverse proxying, but you could use anything related to the above.)

Nabi also doesn't make usage of a ``.env`` file like other related bots. Instead, it processes information from a ``nabi-config.toml``.
If you run the process without the file above, the environment will stop and create one for you. You **MUST** have the
following in your file for the bot to run.

```toml
[discord]
token = "discord_token"
applicationId = "app_id"
defaultGuildId = "default_guild_id"
shards = 999
defaultPrefix = "?"
ownerIds = ["your", "owner", "ids"]
publicKey = "public_key" # This is only necessary if you want to have webserver interactions. 
port = 12212 # This is only necessary if you want to have webserver interactions. 

[database]
addresses = "postgresql_address"
username = "jdbc_username"
password = "jdbc_password"
port = 9203

[cache]
addresses = ["cache", "addresses"]
username = "redis_username"
password = "redis_password"
```

Once you've filled out the following, your bot should be running smoothly! Keep in mind that you may need to tinker more
in order to fine tune Nabi for your specific needs, and that's okay, as Nabi was made to be flexible and diverse.

## Contributing Guidelines
If you are going to contribute, please note the following:

* If there is an issue related to one of our libraries, please report it to the respective repository instead of making a 
workaround. This should only be used in a worst case scenario, and should not **EVER** be excercised.
* We may only accept certain pull requests if it's beneficial to the Nabi ecosystem.
* You should almost **NEVER** target the experimental branch when it comes to pull requests, as we intend on making breaking
changes with this branch.

## Important Contributors
* [yujin](https://github.com/shuuyu) - Primary code maintianer and operator of Nabi.
* [my-name-is-jeff](https://github.com/My-Name-Is-Jeff) - Some idiot who doesn't contribute