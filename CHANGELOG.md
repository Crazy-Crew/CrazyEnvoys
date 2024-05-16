## Changes:
* Updated the permission prefix from `envoy` to `crazyenvoys`
* CrazyEnvoys now supports 1.20.6 Folia but as to if there is a 1.20.6 Folia, You have to wait for SpottedLeaf.

### Optimizations:
* The means of identifying what a flare is when a player clicks one has been optimized, Old flare items will not work.
* Chunks are now loaded async properly.

## Locale Additions:
* Add es-PE.yml to the `locale` folder.

## Fixes:
* Fixed an issue with locale not re-loading properly.

## Internal Changes:
* Update the file manager to be more consistent.
* No longer using deprecated api for spawning the falling block.
* Added a new itembuilder.
* Moved a lot of common code to a `commons` module.

## Removed:
* NBT API is no longer a dependency.

## Other:
* [Feature Requests](https://github.com/Crazy-Crew/CrazyEnvoys/discussions/categories/features)
* [Bug Reports](https://github.com/Crazy-Crew/CrazyEnvoys/issues)