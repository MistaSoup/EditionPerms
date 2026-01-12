# EditionPerms

A lightweight Minecraft plugin for managing permissions separately for Java Edition and Bedrock Edition players on crossplay servers.

## Features

- 🎮 **Separate permissions for Java and Bedrock players**
- 🔍 **Automatic player detection** via username prefix and UUID checking
- 🔄 **Fast reload** - Update permissions without restarting the server
- ⚡ **Folia compatible** - Works on both Paper and Folia servers
- 🛠️ **Simple configuration** - Easy YAML setup
- 🪶 **Lightweight** - Minimal performance impact

## Why was this plugin created?

This plugin was originally created to solve a specific problem on crossplay servers: giving Java Edition players anticheat bypass permissions while keeping Bedrock Edition players under normal anticheat protection. It has since evolved into a simple permission management system for any crossplay server running Geyser/Floodgate.

## Requirements

- Minecraft 1.21.x
- Paper or Folia server
- Java 21+

## Installation

1. Download the latest `EditionPerms-x.x.x.jar` from [Releases](https://github.com/yourusername/EditionPerms/releases)
2. Place the JAR file in your `plugins/` folder
3. Restart your server
4. Edit `plugins/EditionPerms/config.yml` to configure permissions
5. Run `/editionpermsreload` or restart to apply changes

## Configuration

The plugin creates a `config.yml` file in `plugins/EditionPerms/`:
```yaml
# ==================================================================================
# BEDROCK PLAYER DETECTION
# ==================================================================================
# These settings determine HOW the plugin identifies Bedrock players
detection:
  # Bedrock players have this prefix in their username (default: ".")
  # Example: ".PlayerName" is a Bedrock player, "PlayerName" is Java
  bedrock-prefix: "."
  
  # Also check UUID format to detect Bedrock players (recommended: true)
  # Bedrock UUIDs start with "00000000-0000-0000"
  bedrock-uuid-check: true

# ==================================================================================
# PERMISSION GROUPS
# ==================================================================================
# After a player is identified as Java or Bedrock, these groups assign permissions

groups:
  # Give permissions to ALL Java players
  default-java:
    type: java
    prefix: ""  # LEAVE EMPTY to apply to ALL Java players
    permissions:
      - minecraft.command.me
      - minecraft.command.trigger
  
  # Give permissions to ALL Bedrock players
  default-bedrock:
    type: bedrock
    prefix: ""  # LEAVE EMPTY to apply to ALL Bedrock players
    permissions:
      - minecraft.command.me
      - minecraft.command.trigger
```

### Example Configurations

**Give Java players anticheat bypass:**
```yaml
groups:
  default-java:
    type: java
    prefix: ""
    permissions:
      - nocheatplus.shortcut.bypass
      - spartan.bypass
      - essentials.spawn
  
  default-bedrock:
    type: bedrock
    prefix: ""
    permissions:
      - essentials.spawn
```

**Give Java players creative permissions:**
```yaml
groups:
  default-java:
    type: java
    prefix: ""
    permissions:
      - worldedit.selection
      - essentials.fly
      - essentials.gamemode
  
  default-bedrock:
    type: bedrock
    prefix: ""
    permissions:
      - essentials.spawn
```

## Commands

| Command | Aliases | Permission | Description |
|---------|---------|------------|-------------|
| `/editionpermsreload` | `/epr`, `/epreload`, `/editionreload` | `editionperms.reload` | Reloads the config and reapplies all permissions |

## Permissions

- `editionperms.reload` - Allows reloading the plugin configuration (default: OP)

## How It Works

1. **Player joins** → Plugin detects if they're Java or Bedrock
   - Checks username prefix (default: `.` for Bedrock)
   - Checks UUID format (`00000000-0000-0000-xxxx` for Bedrock)

2. **Groups are checked** → Plugin finds matching groups based on player type

3. **Permissions applied** → All matching groups' permissions are granted instantly

## Building from Source

1. Clone the repository:
```bash
git clone https://github.com/yourusername/EditionPerms.git
cd EditionPerms
```

2. Build with Maven:
```bash
mvn clean package
```

3. Find the compiled JAR in `target/EditionPerms-1.0.0.jar`

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

Created with assistance from Claude (Anthropic)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

⭐ If you find this plugin useful, please consider giving it a star on GitHub!
