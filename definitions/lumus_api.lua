---@meta

---Adds packages to a list for import \
---@see doImport
---@param ... string packages
function importPackage(...)  end

---Imports all the packages requested through importPackage
function doImport()  end

---@class meta
---@field name string
---@field displayName string
---@field version string
---@field description string

---@alias server_runner fun(self: any, args: string[])
---@alias client_runner fun(self: any, args: any, player: any)
---@alias event_listener fun(self: any, event: any)

---@class config Configuration of plugin
---@field meta meta Plugin Metadata

---Configure the plugin
---@param configuration config
pluginConfiguration = function(configuration) end

---@class commandData
---@field name string Command name
---@field params string Command parameters
---@field description string Command description
---@field middleware server_runner|client_runner Command middleware. Executed before command and returns status in bool;
---true = continue, false = stop
---@field handler server_runner|client_runner

---Commands registry
commands = {
    ---Register client command
    ---@param data commandData
    clientCommand = function (data) end,

    ---Register server command
    ---@param data commandData
    serverCommand = function (data) end,
}

---Events registry
events = {
    ---Register event listener
    ---@param type string|any Event type, string of class from EventTypes in kebab-case or object \ Example: player-join or PlayerJoin
    ---@param listener fun(self: any, event: any)
    eventListener = function(type, listener) end,

    ---Set the onInit special event\
    ---Called after all plugins have been created and commands have been registered
    ---@param func fun()
    onInit = function(func) end,

    ---Set the onExit special event\
    ---Called when the application exits gracefully, either through Core.app:exit(). It is not called after a crash, unlike onDispose()
    ---@param func fun()
    onExit = function(func) end,

    ---Set the onDispose special event\
    ---Called when the Application is destroyed
    ---@param func fun()
    onDispose = function(func) end
}