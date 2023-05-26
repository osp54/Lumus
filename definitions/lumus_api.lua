---@meta

---@class meta
---@field name string
---@field displayName string
---@field version string
---@field description string
meta = {
    ---Sets plugin name
    ---@param value string
    setName = function (self, value) end,

    ---Sets plugin displayName
    ---@param value string
    setDisplayName = function (self, value) end,

    ---Sets plugin version
    ---@param value string
    setVersion = function (self, value) end,

    ---Sets plugin description
    ---@param value string
    setDescription = function (self, value) end
}

---@alias server_runner fun(self: any, args: string[])
---@alias client_runner fun(self: any, player: any, args: string[])
---@alias event_listener fun(self: any, event: any)

---@class config Configuration of plugin
---@field meta meta Plugin Metadata
---@field onInit fun()
---@field onExit fun()
---@field onDispose fun()
config = {
    ---Defining a command registration with information about your plugin
    modInfoCommand = true,

    ---Register server command
    ---@param name string
    ---@param params string
    ---@param desc string
    ---@param runner server_runner
    ---@overload fun(self: any, name: string, params: string, desc: string, middleware: server_runner, runner: server_runner)
    registerServerCommand = function (self, name, params, desc, runner) end,
    
    ---Register client command
    ---@param name string
    ---@param params string
    ---@param desc string
    ---@param runner client_runner
    ---@overload fun(self, name: string, params: string, desc: string, middleware: client_runner, runner: client_runner)
    registerClientCommand = function (self, name, params, desc, runner) end,

    ---Register event listener
    ---@param event_name string
    ---@param listener event_listener
    ---@overload fun(self, event: any, listener: event_listener)
    registerEventListener = function (self, event_name, listener) end,

    ---Set the onInit special event\
    ---Called after all plugins have been created and commands have been registered
    ---@param func fun()
    setOnInit = function (self, func) end,

    ---Set the onExit special event\
    ---Called when the application exits gracefully, either through Core.app:exit(). It is not called after a crash, unlike onDispose()
    ---@param func fun()
    setOnExit = function (self, func) end,

    ---Set the onDispose special event\
    ---Called when the Application is destroyed
    ---@param func fun()
    setOnDispose = function (self, func) end,
}