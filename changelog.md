## What's changed?
* Fixed an issue with DecentHolograms error due to using characters not allowed. [#154](https://github.com/Crazy-Crew/CrazyEnvoys/pull/154)
* Fixed an issue by removing ignoreCancelled(true) which allows blocks like redstone torches to work while right-clicking air [#156](https://github.com/Crazy-Crew/CrazyEnvoys/pull/156)
* Fixed a visual issue with the #sendBlockChange by delaying it by 2 ticks which is used to show the bedrock when placing or being in edit mode. [#155](https://github.com/Crazy-Crew/CrazyEnvoys/pull/155)
* Added a new feature that allows the player who started the signal flare to be announced that they started it in chat. [#158](https://github.com/Crazy-Crew/CrazyEnvoys/pull/158)
    * This adds a new message to your message file, and a new config.yml option.
* Fixed an issue with data not saving to data.yml

*All new config.yml options have values that represent how they were out of the box previously*

### [#157](https://github.com/Crazy-Crew/CrazyEnvoys/pull/157)
* Added a new argument to the `/crazyenvoys ignore` command which is `-s` or -`no`
* Added the ability to turn off/on broadcasting via a new config.yml option.

## New Contributors
* [@InstantlyMoist](https://github.com/InstantlyMoist)