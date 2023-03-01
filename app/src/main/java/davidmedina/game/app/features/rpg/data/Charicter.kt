package davidmedina.game.app.features.rpg.data

import davidmedina.game.app.features.rpg.data.ability.Ability
import davidmedina.game.app.features.rpg.data.ability.DamageType
import davidmedina.game.app.features.rpg.data.ability.Stat
import davidmedina.game.app.features.rpg.data.ability.StatusEffect


val spaceAlien = Character(
    name = "Zorg",
    characterID = CharacterId.Alien,
    hp = DiminishableStates(300, 300),
    will = DiminishableStates(100, 100),
    strength = 50,
    defense = 20,
    speed = 1.5f,
    mind = 30,
    ability = listOf(
        Ability.Offensive("Laser Blast", DamageType.Physical, 8F, Stat.Strength, 6),
        Ability.Offensive("Telekinetic Throw", DamageType.Psychic, 5F, Stat.Mind, 8),
        Ability.Buff(
            "Shield Boost",
            DamageType.Physical,
            StatusEffect.Buffed(Stat.Defense, 5, 3),
            5
        ),
        Ability.Taunt(3, "Aggro Shout", 3),
        Ability.Stealth("Cloak", 2)
    )
)