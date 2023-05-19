Lumus
=====

Lumus is a Mindustry plugin that loads Lua plugins.

Contents
--------
*   Usage
	*   [Install the Plugin](#install-the-plugin)
	*   [Installing Lua Plugins](#installing-lua-plugins)
*   [Developer Guide](#developer-guide)
    *   [Configuring](#configuring)
    *   [Registering Commands](#registering-commands)
	    *  [Commands Middleware](#commands-middleware)
    *   [Listening Events](#listening-events)

### Install The Plugin

To install the plugin, follow these steps:

1.  Download the `Lumus.jar` from the latest release.
2.  Put the downloaded file in the server mods folder located at `config/mods`.

### Installing Lua Plugins

To install Lua plugins, do the following:

1.  Unpack the plugin archive.
2.  Copy the extracted files to the `server/config/luamods` directory.

Developer Guide
---------------

To start developing your own plugin, follow these steps:

1.  Create a folder named `%plugin-name%` inside the `config/luamods` directory.
2.  Inside the created folder, create a file named `main.lua`. This file will serve as the entry point of your plugin.

### Configuring

In your `main.lua` file, add the following code:

```lua
configure = function (config)
    config.meta:setDisplayName("Example Plugin")
    config.meta:setName("example-plugin")
    config.meta:setVersion("1.0")
    config.meta:setDescription("Example plugin")
end
```

Explanation of the configuration options:

*   `config.meta`: Provides metadata about the plugin.
*   `config.meta:setDisplayName(str)`: Sets the display name of the plugin, which is used in the `luamods` console command.
*   `config.meta:setName(str)`: Sets the internal name of the plugin, used for interaction with other plugins.
*   `config.meta:setDescription(str)`: Sets the description of the plugin, which is used in the `%plugin_name%` command.

### Registering Commands

To register commands, add the following code to the [configure](#configuring):

**Client Command**

```lua
config:registerClientCommand("example-client-command", "", "Example client command", function (this, args, player)
    player:sendMessage("Hello!")
end)

```

**Server Command**
```lua
config:registerServerCommand("example-server-command", "", "Example server command", function (this, args)
    Log:info("Hello!")
end)
```

These commands demonstrate how to register client and server commands. The example client command sends a "Hello!" message to the player, while the example server command logs "Hello!" using the Log module.

### Commands middleware
Commands middleware is a function that is executed before a command and returns a boolean value. If the value is  true, the execution of the command continues. Otherwise, the command is ignored.

Add following code to [configure](#configuring):
**Simple command for pre-check for admin**
```lua
config:registerClientCommand("admin-command", "", "Admin Command",  isAdmin, function (this, args, player)
	player:sendMessage("You are admin!")
end)

function isAdmin(this, args, player)
	return player.admin;
end
```
**Simple command for pre-check for server status**
```lua
config:registerServerCommand("kill-all", "", "Kill all units", serverIsHosting, function (this, args)
	Groups.unit:each(function (this, unit) unit:kill() end)
	Log:info("Success")
end)

function serverIsHosting(this, args)
	return not Vars.state:isMenu()
end
```
These examples demonstrate how to use commands middleware to perform pre-checks before executing a command. The `isAdmin` function checks if the player is an admin before executing the "admin-command" client command. The `serverIsHosting` function checks if the server is not in the menu state before executing the "kill-all" server command.

### Listening events
There are two ways to listening to events

**First**:
```lua
Events:on(PlayerJoinEvent, function (this, event) 
	event.player:sendMessage("Welcome to our server!")
end)
```
This is a more raw way of listening to events using the internal Mindustry Java API.
**Second**:
In [configure](#configuring), add the following code:
```lua
config:registerEventListener("player-join-event", function (this, event)
	event.player:sendMessage("Welcome to our server!")
end)
```
This is a more Lua-like way of listening to events, where the event name is in kebab case.
