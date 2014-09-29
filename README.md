ttCore [![Build Status](http://ci.tterrag.com/job/ttCore/badge/icon)](http://ci.tterrag.com/job/ttCore/)
======

WIP core lib for my other mods. Name also WIP...

___
###Things this mod does on its own:

* Adds a config option to enable tooltips that show all oredict registrations on an item
* Adds a magical crops WAILA plugin
* Adds a '/reloadConfigs' command to reload configs from disk of any (supported) mod
* Adds a config to remove void fog
* Adds recipes from 2 slabs -> full block
* Fixes a few vanilla annoyances (boats now stack to 16, beds have the correct sounds)

###Things this mod does for other modders
* Many useful rendering methods, for example rotation and ISBRH obj rendering
* A Compatabiltiy Registry for classes that should only load with other mods present
* A class for simpler localization of arbitrary strings
* Some utilities for copying files out of jars and extracting zip files
* Some utilities for using JSON
* An annotation based handler system, no registration of event handlers needed!
* A class to assemble your own resourcepack and inject it into the game
* A simple CreativeTab to use for your mod
