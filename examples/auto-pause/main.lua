local enabled = true

---@param config config
configure = function (config)
    config.meta:setDisplayName("Auto Pause")
    config.meta:setName("auto-pause")
    config.meta:setVersion("1.0")
    config.meta:setDescription("A plugin written in lua to automatically pause the game if the server is empty")

    config:registerServerCommand("autopause", "<on/off>", "Toggle autopause", function(self, args)
        enabled = args[1] == "on"
        Log:info("Turned " .. (enabled and "on" or "off"))
    end)
    config:registerEventListener("play-event", function(this, event)
        if not enabled then return end
        if Groups.player:size() == 0 and not Vars.state:isPaused() then
            Vars.state:set(GameState.State.paused)
            Log:info("Paused")
        end
    end)
    config:registerEventListener("player-join", function (this, event)
        if not enabled then return end

        if Vars.state:isPaused() then
            Vars.state:set(GameState.State.playing)
            Log:info("Unpaused")
        end
    end)
    config:registerEventListener("player-leave", function(this, event)
        if not enabled then return end

        if Groups.player:size() == 1 then
            Vars.state:set(GameState.State.playing)
            Log:info("Paused")
        end
    end)
end