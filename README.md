# Nabi
A bot that specializes in ease of use, and versatility.

## Features

## Hosting
Before running Nabi, please note that you will not be given support if something breaks/goes wrong. The source code is 
available purely for educational purposes. Nabi was originally created to be a closed-sourced, private bot. Nabi was 
specifically made for larger operations/guilds, so please take this into mind when looking at the source code.

With the notice out of the way, you will need the following: 
* Java 17 (This is the minimum version, since we are using Java 15 API for webservers.)
* PostgreSQL (The database we use.)
* ~~An Interactions Endpoint~~ (We have removed webserver capability this for the time being. However, this will come back at a later date.)

Nabi should automatically create a config file for you (titled ``nabi.conf``), and exit the process. You will need to 
fill out **ALL** the information. Otherwise, Hocon will not be able to parse the file correctly. 

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

Once you've filled out all the required information, relaunch your bot.