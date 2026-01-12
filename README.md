# EditionPerms

A lightweight Minecraft plugin for managing permissions separately for Java Edition and Bedrock Edition players on crossplay servers.

## Features

- 🎮 **Separate permissions for Java and Bedrock players**
- 🔍 **Automatic player detection** via username prefix and UUID checking
- 👥 **Multiple permission groups** with custom name prefixes
- 🔄 **Hot reload** - Update permissions without restarting the server
- ⚡ **Folia compatible** - Works on both Paper and Folia servers
- 🛠️ **Simple configuration** - Easy YAML setup with clear examples

## Why was this plugin created?

This plugin was originally created to solve a specific problem on crossplay servers: giving Java Edition players anticheat bypass permissions while keeping Bedrock Edition players under normal anticheat protection. It has since evolved into a flexible permission management system for any crossplay server running Geyser/Floodgate.

## Requirements

- Minecraft 1.21+
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
# Bedrock player detection settings
detection:
  bedrock-prefix: "."  # Bedrock players have "." prefix in their username
  bedrock-uuid-check: true  # Also check UUID format

# Permission groups
groups:
  # All Java players get these permissions
  default-java:
    type: java
    prefix: ""  # Empty = all Java players
    permissions:
      - minecraft.command.me
      - some.other.permission
  
  # All Bedrock players get these permissions
  default-bedrock:
    type: bedrock
    prefix: ""  # Empty = all Bedrock players
    permissions:
      - minecraft.command.me
      - different.permission
```

### Advanced Usage

You can create groups for specific players using name prefixes:
```yaml
groups:
  # VIP players (both Java and Bedrock with "VIP_" prefix)
  vip-players:
    type: all
    prefix: "VIP_"
    permissions:
      - essentials.fly
      - essentials.speed
  
  # Moderators (Java players with "Mod_" prefix)
  moderators:
    type: java
    prefix: "Mod_"
    permissions:
      - essentials.kick
      - essentials.ban
```

### Group Options

- **type**: `java`, `bedrock`, or `all`
- **prefix**: Username prefix to match (leave empty for all players of that type)
- **permissions**: List of permissions to grant

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

2. **Groups are checked** → Plugin finds matching groups based on:
   - Player type (Java/Bedrock)
   - Username prefix (if specified)

3. **Permissions applied** → All matching groups' permissions are granted instantly

## Examples

### Basic Crossplay Setup
```yaml
groups:
  default-java:
    type: java
    prefix: ""
    permissions:
      - nocheatplus.bypass  # Java players bypass anticheat
      - essentials.spawn
  
  default-bedrock:
    type: bedrock
    prefix: ""
    permissions:
      - essentials.spawn  # Bedrock players don't get bypass
```

### VIP Ranks
```yaml
groups:
  vip-java:
    type: java
    prefix: "VIP_"
    permissions:
      - essentials.fly
      - worldedit.selection
  
  vip-bedrock:
    type: bedrock
    prefix: ".VIP_"  # Note: Bedrock prefix includes the "."
    permissions:
      - essentials.fly
```

### Staff Permissions
```yaml
groups:
  staff:
    type: all  # Both Java and Bedrock
    prefix: "Staff_"
    permissions:
      - essentials.kick
      - essentials.mute
      - essentials.teleport
```

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

3. Find the compiled JAR in `target/EditionPerms-x.x.x.jar`

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
