## Changes 🔨
- Updated the internal command implementation, the changes are minimal... and are listed below!
  - `envoy.admin` is no longer a valid permission for commands
- Replaced all instances of legacy color codes with MiniMessage
  - The only places *not* touched are the hologram plugin's and their color implementations.
- Added /crazyenvoys migrate -mt [LegacyColorAll/MojangMappedAll] **which** you must run.
  - This migrates all color codes to MiniMessage.
- Improved runtime placeholder migration.
- Improved initial prize creation.
- Updated default configurations.
  - All materials are now lowercase, existing configurations *should* work.
  - All enchantments are now mojang mapped, existing configurations *should* work.
    - SHARPNESS → sharpness, and so on.
- Improved folia support maybe?

## Bugs Fixed 🐛
- Fixed an issue with CMI Holograms by forking CMI and updating the API ourselves.

As always, Report 🐛 to https://github.com/Crazy-Crew/CrazyEnvoys/issues