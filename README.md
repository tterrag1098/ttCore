#ttCore [![Build Status](http://ci.tterrag.com/job/ttCore/badge/icon)](http://ci.tterrag.com/job/ttCore/)
####[Download](http://minecraft.curseforge.com/mc-mods/226082-ttcore)
======

WIP core lib for my other mods. Name also WIP...

___
###Things this mod does on its own:

* Adds a config option to enable tooltips that show all oredict registrations on an item
* ~~Adds a magical crops WAILA plugin~~ (Moved to [WailaPlugins](https://github.com/tterrag1098/WAILAPlugins)!)
* Adds a '/reloadConfigs' command to reload configs from disk of any (supported) mod
* Adds a config to remove void fog
* Adds recipes from 2 slabs -> full block
* Fixes a few vanilla annoyances (boats now stack to 16, beds have the correct sounds)
* Adds a config to change the max level on anvil repairs (no more "Too Expensive!")
* Adds a command to query a scoreboard score for a player
* Adds an "XP Boost" enchant that increases XP dropped from killed entities and broken blocks
* Adds a *special something* for achievements

###Things this mod does for other modders
* Many useful rendering methods, for example rotation and ISBRH obj rendering
* Base classes for ISBRH and TESR renderers for simple modeled blocks
* A Compatabiltiy Registry for classes that should only load with other mods present
* A class for simpler localization of arbitrary strings
* Some utilities for copying files out of jars and extracting zip files
* Some utilities for using JSON
* An annotation based handler system, no registration of event handlers needed!
* A class to assemble your own resourcepack and inject it into the game
* A simple CreativeTab to use for your mod
* A BlockCoord class (yes, another one)
* Block iterators for common use cases, such as a cubic iterator and planar iterator. These are used to iterate over the blocks in an area without using nasty nested for loops.
* An easy-to-use config system that automatically handles a lot of the "boring" stuff in most config handlers, such as responding to events and the config GUI. An example of how to use the system can be found [here](https://github.com/tterrag1098/WAILAPlugins/tree/master/src/main/java/tterrag/wailaplugins/config)
* A simple way to schedule events to happen in a certain amount of ticks

___
## How do I use this thing?

It's quite easy. First, you will need to add my maven repository to the ones that gradle searches in. In your build.gradle, just after the `minecraft` block you should have, add this:
```
repositories {
  maven { // ttCore
    name 'tterrag Repo'
    url "http://maven.tterrag.com/"
  }
}
```

If your build.gradle already has the `dependencies` section, just add the `maven` section to the existing block.

Next, you need to specify what mod to download from the maven. We do this in the `dependencies` block:

```
dependencies {
  compile "tterrag.core:ttCore:MC1.7.10-0.0.3-32:deobf"
}
```

Add this block directly after your `repositories` block. The version (part before `:deobf`) can be changed to whatever version your mod requires.
