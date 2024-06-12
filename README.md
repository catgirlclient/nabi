# Nabi
A bot that specializes in ease of use, and versatility.

## Features
* Automoderation via events
* Useful Slash Commands and Chat Commands
* An actually good permissions system
* Completely free (Looking at you Mee6)

## Planned features
* [ ] Session Checkpoints

## Hosting
Before running Nabi, please note that you will not be given support if something breaks/goes wrong. The source code is 
available purely for educational purposes. Nabi was originally created to be a closed-sourced, private bot, 
and was specifically made for large servers in mind, so please take this into mind when looking at the source code.

With the notice out of the way, you will need the following: 
* Java 17 (This is the minimum version, since we are using Java 15's API for webservers)
* PostgreSQL (The database we use)
* ~~An Interactions Endpoint~~ (We have removed webserver this for the time being. However, this will come back at a later date.)

Nabi should automatically create a config file for you (titled ``nabi.conf``), and exit the process upon running. You will need to 
fill out **ALL** the information. All the information is required, and cannot be null.

Your file should look something like this: 

```hocon
{
  token: "token"
  applicationId = "app_id"
  defaultGuild = "guild_id"
  shards = 1 
  publicKey = "public_key"
  port = 12212
  jdbcUsername = "db_username"
  jdbcPassword = "db_password"
  jdbcUrl = "db_url"
  
  // For Chat Commands
  prefix = ["!", "?", "/"] // not added as of right now
  
  // For Developer Commands
  owners = ["user_id"] // not added as of right now
  
  // Setting this to development will send all of the debug logs.
  // Set this to production if you don't want to see it.
  environment = "development" // Not added as of right now
}
```

Once you've filled out all the required information, relaunch your bot. Your bot should be running perfectly from then-on.

## Credits
Nabi would not be possible without the following libraries/projects.
* [Kord](https://github.com/kordlib/kord)
* [Discord InteraKTions](https://github.com/catgirlclient/DiscordInteraKTions)

___
<p style="text-align: center">
    Copyright (c) 2024 CatgirlClient & Shuuyu. Nabi is licensed under the PolyForm Shield License 1.0.0 unless otherwise stated. All rights reserved.
</p>
