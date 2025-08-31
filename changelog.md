## What's changed?
- Fixed an issue with default tier files getting extracted despite deleting them.
- No longer allow `0 seconds` being added to time related strings [#162](https://github.com/Crazy-Crew/CrazyEnvoys/pull/162)
- Fixed an issue with random spawn behavior by using the correct config option
```yml
envoys:
  generation:
    # Whether to always spawn the max amount of crates possible set below, If the option is false. Spawn locations set using /envoy edit will instead spawn.
    # Note: You should only use this if random-locations is false.
    max-drops-toggle: false
    # The max amount of crates that will spawn.
    max-drops-amount: 20
    # The min amount of crates that will spawn.
    min-drops-amount: 7
    # This option will spawn a random number of envoys between min-drops-amount and max-drops-amount.
    # Note: You must set max-drops-toggle to false otherwise this will not function.
    random-drops: true # -> This is the option it used before.
    # Random location settings
    random-locations:
      toggle: true # -> This is the option it uses now.
      max-radius: 300
      min-radius: 20
```