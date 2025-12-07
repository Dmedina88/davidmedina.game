# Future Features Implementation Plan

## 1. New Game Plus (NG+) & Stats Tracking
**Goal**: Allow players to carry over progress after victory and track their achievements.
- [ ] **Stats Tracking**:
    - Add `victoryCount` to `ClassicDungeonState`.
    - Add `highestFloorReached` to `ClassicDungeonState`.
    - Track `totalEnemiesDefeated` across runs?
- [ ] **New Game Plus Logic**:
    - Upon Victory (Level 50+), offer "Ascend / NG+".
    - Reset Dungeon Level to 1.
    - **Keep**: Player Level, Stats, Inventory, Gold.
    - **Scale**: Enemies in NG+ should be stronger (e.g., level * (1 + victoryCount * 0.5)).
    - **Rewards**: Maybe a permanent "Ascension Buff"?

## 2. Lore System
**Goal**: increasing immersion with flavor text.
- [ ] **Enemy Lore**:
    - Add `description` or `lore` field to `DungeonEnemy`.
    - Print this message when `enterCombat` occurs (first time encountering?).
- [ ] **Tile/Environment Lore**:
    - Triggers when entering specific rooms (e.g., "You enter a chamber smelling of ozone and rot.").
    - Triggers for special tiles (Fountains, Statues).

## 3. UI Updates
- [ ] Update Victory Screen to show Stats and "New Game +" button.
- [ ] Display current "NG+ Cycle" (e.g., "Cycle: 2") in the HUD.
