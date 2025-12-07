package davidmedina.game.app.features.antigravity.dungeon.planning

/**
 * Workflow for Economy Update: Shops & Inns
 *
 * GOAL: Spend gold on gear and buffs.
 *
 * 1. NEW TILES:
 *    - SHOP (💰): Sell potions and random gear.
 *    - INN (🏨): Heal HP/MP and grant buffs.
 *
 * 2. SHOP SYSTEM:
 *    - Buy Items: Generated randomly based on dungeon level.
 *    - Sell Items: Get 50% value back.
 *
 * 3. INN SYSTEM:
 *    - "Basic Rest": 10g -> Heal 50% HP/MP.
 *    - "Luxury Suite": 50g -> Full Heal + "Well Rested" Buff (+10% Stats for 50 turns).
 *    - "Warrior's Feast": 100g -> "Berzerk" Buff (+20% Atk for 30 turns).
 *
 * 4. BUFF SYSTEM:
 *    - Player needs a list of ActiveBuffs.
 *    - Buffs tick down every turn.
 */
class EconomyUpdatePlan {
}
