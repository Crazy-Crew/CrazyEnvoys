### Added:
- Added support for HeadDatabaseAPI
```yaml
Prizes: #The prizes that can be won.
  '1':
    DisplayName: '&6Custom Head' #The display name used in %reward%
    Chance: 75
    Drop-Items: false
    Messages:
      - '&7You have just won {reward}.'
    Items:
      # Only use this if you have HeadDatabase by Arcaniax on the server.
      - 'Item:PLAYER_HEAD, Skull:61151, Name:&cA fancy head, Lore:&eA fancy lore,&7with lines, Amount:3'
```

### Changes:
- Removed NBT API completely (which was never used internally just shaded for some stupid reason)
- Flares are no longer identified by if the ItemStack object ids match which was hefty, they now check for a tag added by PersistentDataContainer
  - There is no way to migrate old Flares given because they have no unique identifier we can know of.