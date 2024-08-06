<h1 style="text-align: center">
    Nabi 🦋
</h1>
<h5 style="text-align: center">
    Nabi is a powerful Discord bot specifically made for the purpose of modernity and simplicity made with Kotlin.
</h5>

## Features
* Automoderation via events
* Useful Slash Commands and Chat Commands
* An actually good permissions system
* Completely free (Looking at you Mee6)

## Planned features
* [ ] Session Checkpoints

## Hosting
> [!CAUTION]
> You will not be given support if Nabi's code breaks or something goes wrong. Nabi's source code is available purely for 
> educational purposes, and was originally meant to be closed sourced and private. Nabi was intentionally made for
> larger servers, so please keep that in mind when looking at the source code.

With the notice out of the way, you will need the following: 
* Java 17 (This is the minimum version, since we are using Java 15's API for webservers)
* PostgreSQL (The database we use)
* ~~An Interactions Endpoint~~ (We have removed webserver this for the time being. However, this will come back at a later date.)

Nabi should automatically create a config file for you (titled ``nabi.conf``), and exit the process upon running. You will need to 
fill out **ALL** the information. All the information is required, and cannot be null.

Your file should look something like this: 

```hocon
{
  discord = {
    token = "token"
    applicationId = "app_id"
    defaultGuildId = "default_guild"
    shards = 10
    defaultPrefix = "<3"
    ownerIds = ["ownerId1", "ownerId2"] // For developer commands
    publicKey = "public_key" // For webservers
    port = 12212
  }
  
  database = {
    addresses = "postgres" // The postgres database you're targetting
    username = "someone"
    password = "passwordOfSomeone"
  }

  redis = {
    address = ["address1", "address2"] // If you are not planning to shard, just have one.
    username = "username"
    password = "password"
  }
  
  // Setting this to development will send all of the debug logs.
  // Set this to production if you don't want to see it.
  environment = "development" // Not added as of right now
}
```

Once you've filled out all the required information, relaunch your bot.

___
<h5 style="text-align: center">
    Copyright (c) 2024 CatgirlClient & Shuuyu. Nabi is licensed under the PolyForm Shield License 1.0.0 unless otherwise stated. All rights reserved.
</h5>
