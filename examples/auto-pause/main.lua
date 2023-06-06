pluginConfiguration {
    meta = {
        displayName = "Auto Pause",
        name = "auto-pause",
        version = "1.0",
        description = "A plugin written in lua to automatically pause the game if the server is empty"
    }
}

local enabled = true

commands.serverCommand {
    name = "autopause",
    params = "<on/off>",
    description = "Toggle autopause",
    handler = function(self, args)
        enabled = args[1] == "on"
        Log:info("Turned " .. (enabled and "on" or "off"))
    end
}

events.eventListener("play-event", function(this, event)
    if not enabled then return end
    if Groups.player:size() == 0 and not Vars.state:isPaused() then
        Vars.state:set(GameState.State.paused)
        Log:info("Paused")
    end
end)

events.eventListener("player-join", function (this, event)
    if not enabled then return end

    if Vars.state:isPaused() then
        Vars.state:set(GameState.State.playing)
        Log:info("Unpaused")
    end
end)

events.eventListener("player-leave", function(this, event)
    if not enabled then return end

    if Groups.player:size() == 1 then
        Vars.state:set(GameState.State.playing)
        Log:info("Paused")
    end
end)