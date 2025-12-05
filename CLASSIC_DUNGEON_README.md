# 🗡️ Classic Dungeon - Traditional Roguelike RPG

> **Note**: This is the **Classic** version using traditional procedural generation. A separate **ML Dungeon** feature will be created in the future that uses machine learning for procedural generation, enemy AI, and adaptive difficulty.

## Overview
**Classic Dungeon** is a classic grid-based roguelike dungeon crawler with traditional touch controls. Built with a clean architecture designed for **future ML enhancements** in procedural generation, enemy AI, and adaptive difficulty.

## 🎮 Game Features

### Core Gameplay
- **Turn-Based Combat**: Strategic RPG battles with attack, defend, magic, and flee options
- **Grid-Based Movement**: Classic roguelike navigation with D-pad controls
- **Procedural Dungeons**: Randomly generated levels using BSP room generation
- **Character Progression**: Level up system with stat increases
- **Inventory System**: Collect, equip weapons and armor
- **Multiple Enemy Types**: Rats, Goblins, Skeletons, Orcs, and Dragons
- **Fog of War**: Vision system with explored/visible tiles

### Controls
- **D-Pad**: Move character (Up/Down/Left/Right)
- **🎒 Bag Button**: Open inventory
- **Combat Actions**:
  - ⚔️ Attack: Basic attack
  - 🛡️ Defend: Temporary defense boost (+5 DEF)
  - ✨ Magic: Powerful attack (15 mana, 1.5x damage)
  - 🏃 Flee: Attempt to escape (70% success)

### RPG Systems
- **Stats**:
  - HP (Health Points)
  - MP (Mana Points)
  - Attack: Base damage
  - Defense: Damage reduction
  - Level & Experience
  - Gold currency

- **Equipment**:
  - Weapons: Increase attack power
  - Armor: Increase defense
  - Items stack in inventory until equipped

- **Combat**:
  - Damage = Attack - (Defense / 2) ± variance
  - Turn-based: Player acts, then enemy counter-attacks
  - Magic attacks bypass some defense

## 🏗️ Architecture

### Files Structure
```
MLDungeonViewModel.kt (670 lines)
├── Game State Management
├── Turn-Based Combat Logic
├── Procedural Generation (BSP algorithm)
├── Enemy AI (proximity-based)
├── Inventory & Equipment
└── Character Progression

MLDungeonScreen.kt (656 lines)
├── Exploring View (dungeon map + controls)
├── Combat View (combat UI)
├── Inventory View (equipment & items)
├── Level Up Screen
├── Game Over Screen
└── Victory Screen
```

### Data Models
- **`GridPosition`**: 2D coordinate system
- **`DungeonTile`**: Floor, Wall, Stairs with fog of war
- **`Player`**: Character stats, inventory, equipment
- **`DungeonEnemy`**: Enemy stats, position, AI
- **`Item`**: Weapons, armor, potions, gold

### Game States
```kotlin
enum class DungeonGameState {
    EXPLORING,    // Navigate dungeon, find enemies/items
    IN_COMBAT,    // Turn-based battle
    INVENTORY,    // Manage equipment
    GAME_OVER,    // Player defeated
    LEVEL_UP,     // Stat increase screen
    VICTORY       // (Future) Complete dungeon
}
```

## 🤖 ML Integration Points (Future)

### Planned ML Features

#### 1. **Procedural Generation (ML-Enhanced)**
- **Current**: Simple BSP (Binary Space Partitioning) algorithm
- **Future**: 
  - Train generative model on dungeon patterns
  - Use GANs or VAEs for level design
  - Learn from player behavior to create optimal difficulty curves
  - Generate themed dungeons based on player preferences

#### 2. **Enemy AI (Adaptive)**
- **Current**: Simple pathfinding toward player
- **Future**:
  - Reinforcement Learning for tactical AI
  - Enemies learn player patterns
  - Adaptive difficulty based on win/loss rate
  - Personality profiles (aggressive, defensive, tactical)

#### 3. **Loot Generation (Intelligent)**
- **Current**: Random item drops
- **Future**:
  - ML-driven loot tables based on player progression
  - Balance weapons/armor for current difficulty
  - Predict player needs from inventory patterns
  - Rarity adjustment based on skill level

#### 4. **Difficulty Balancing**
- **Current**: Linear stat scaling per level
- **Future**:
  - Dynamic difficulty adjustment (DDA) using player performance
  - Meta-learning across multiple playthroughs
  - Personalized challenge curves
  - Predict optimal XP/gold rewards

#### 5. **Content Generation**
- **Future**:
  - Procedural item generation (unique weapons/armor)
  - Generate enemy types with ML-balanced stats
  - Create dungeon themes/biomes
  - Generate quest objectives

### ML Integration Architecture
```
Current: Basic procedural → ML Model → Enhanced procedural
              ↓                           ↓
         [Seed/Rules]              [Learned Patterns]
```

## 🎯 How to Add ML Features

### Example: ML-Powered Enemy AI

```kotlin
// Current simple AI
private fun moveEnemyTowardsPlayer(enemy: DungeonEnemy) {
    // Basic pathfinding
}

// Future ML AI
private fun mlEnemyDecision(enemy: DungeonEnemy): Action {
    val state = encodeGameState(enemy)  // Convert to tensor
    val action = mlModel.predict(state)  // RL model decides action
    return decodeAction(action)
}
```

### Example: Procedural Generation

```kotlin
// Current
private fun generateDungeon(width: Int, height: Int, level: Int) {
    // BSP algorithm
}

// Future with ML
private fun mlGenerateDungeon(level: Int, playerStats: PlayerStats): Dungeon {
    val seed = mlModel.generateSeed(level, playerStats)
    return enhancedProcGen(seed)
}
```

## 📊 Current Game Balance

### Enemy Stats (Base + Level Scaling)
| Enemy     | HP     | Attack | Defense | XP Reward |
|-----------|--------|--------|---------|-----------|
| Rat       | 15+5L  | 5+2L   | 2+L     | 30        |
| Goblin    | 25+5L  | 5+2L   | 2+L     | 50        |
| Skeleton  | 35+5L  | 5+2L   | 2+L     | 70        |
| Orc       | 45+5L  | 5+2L   | 2+L     | 90        |
| Dragon    | 80+5L  | 5+2L   | 2+L     | 160       |

### Player Progression
- **Start**: HP 100, MP 50, ATK 10, DEF 5
- **Per Level**: HP +20, MP +10, ATK +3, DEF +2
- **XP Needed**: 100 + (50 × level)

### Item Stats (Example)
- **Iron Sword**: +5-15 Attack (scales with dungeon level)
- **Leather Armor**: +3-13 Defense (scales with dungeon level)
- **Health Potion**: Restore 30 HP
- **Gold Pile**: 10-50 gold

## 🎨 Visual Design

### Tile Colors
- Floor: Dark gray (#2a2a2a)
- Wall: Medium gray (#555555)
- Stairs: Yellow
- Fog of War: 50% alpha revealed tiles

### Entity Colors
- Player: Cyan with yellow outline
- Rat: Brown
- Goblin: Green
- Skeleton: White
- Orc: Red
- Dragon: Magenta

### UI Theme
- Dark translucent cards (80% alpha black)
- Yellow text for important stats
- Cyan for attack/defense values
- Color-coded progress bars (Red=HP, Blue=MP)

## 🚀 Future Roadmap

### Phase 1: Polish (Current)
- ✅ Core gameplay loop
- ✅ Turn-based combat
- ✅ Procedural generation
- ✅ Inventory system
- ⏳ Item  pickup from map
- ⏳ Multiple dungeon themes

### Phase 2: ML Integration
- 🔮 Train RL model for enemy AI
- 🔮 Procedural generation VAE
- 🔮 Dynamic difficulty adjustment
- 🔮 Player behavior analysis

### Phase 3: Content Expansion
- 🔮 More enemy types
- 🔮 Boss battles
- 🔮 Special abilities/spells
- 🔮 Quest system
- 🔮 Achievements

### Phase 4: Advanced ML
- 🔮 Meta-learning across players
- 🔮 Personalized content generation
- 🔮 Predictive balancing
- 🔮 Emergent gameplay patterns

## 🛠️ Technical Notes

### Procedural Generation Algorithm
Uses **Binary Space Partitioning (BSP)**:
1. Generate random rooms with collision detection
2. Connect rooms with L-shaped corridors
3. Place stairs in final room
4. Spawn enemies/items in rooms

### Fog of War System
- **Revealed**: Tile has been seen before (permanent)
- **Visible**: Tile is currently in vision range (5 tiles)
- Enemies/items only shown when visible

### Turn System
1. Player moves/acts
2. Check for enemy collision → enter combat
3. If exploring, process enemy turns
4. Enemies move toward player if in range (5 tiles)
5. Adjacent enemies trigger combat

## 📈 Metrics for ML Training (Future)

Collect these metrics for ML model training:
- Player movement patterns
- Combat success rate per enemy type
- Time to complete levels
- Death locations/causes
- Equipment choices
- Resource management (HP/MP usage)
- Gold spending patterns

## 💡 Development Notes

### Why This Architecture?
- **Separation of Concerns**: ViewModel handles logic, Screen handles UI
- **Testable**: Pure functions for combat, generation
- **Extensible**: Easy to swap procedural gen with ML models
- **State-Driven**: Single source of truth in `MLDungeonState`
- **Reactive**: Flow-based state updates

### Adding New Features
1. **New Enemy**: Add to `generateEnemies()` with stats
2. **New Item**: Add to `ItemType` enum and `generateItems()`
3. **New Tile**: Add to `TileType` and update rendering
4. **ML Model**: Create wrapper in ViewModel, call from game loop

---

**Classic Dungeon** combines classic roguelike gameplay with modern Android architecture, creating a solid foundation for innovative ML-powered features. The game is ready to play NOW while being perfectly positioned for future AI enhancements! 🗡️🤖
