package davidmedina.game.app.features.ai_musemum.tactical

import java.lang.Integer.max
import java.lang.Integer.min

data class Skill(
    // A property to store the name of the skill
    val name: String,
    // A property to store the range of the skill
    val range: Int,
    // A property to store the area of effect size of the skill
    val aoeSize: Int,
    // A property to store the effect of the skill as a lambda function that takes two parameters: the user and the target of the skill
    val effect: (TacticalCharacter, TacticalCharacter) -> Pair<TacticalCharacter, TacticalCharacter>
)

// Skills for the Warrior class
val warriorSkill1 = Skill("Power Strike", 1, 1) { user, target ->
    val damage = user.atk * 2 - target.def
    val updatedTarget = target.copy(hp = max(0, target.hp - damage))
    Pair(user, updatedTarget)
}

val warriorSkill2 = Skill("Whirlwind", 1, 2) { user, target ->
    val damage = user.atk - target.def
    val updatedTarget = target.copy(hp = max(0, target.hp - damage))
    Pair(user, updatedTarget)
}

// Skills for the Mage class
val mageSkill1 = Skill("Fireball", 3, 1) { user, target ->
    val damage = user.atk * 1.5 - target.def
    val updatedTarget = target.copy(hp = max(0, target.hp - damage.toInt()))
    Pair(user, updatedTarget)
}

val mageSkill2 = Skill("Blizzard", 3, 3) { user, target ->
    val damage = user.atk - target.def
    val updatedTarget = target.copy(hp = max(0, target.hp - damage))
    Pair(user, updatedTarget)
}

// Skills for the Rogue class
val rogueSkill1 = Skill("Backstab", 1, 1) { user, target ->
    val damage = user.atk * 2.5 - target.def
    val updatedTarget = target.copy(hp = max(0, target.hp - damage.toInt()))
    Pair(user, updatedTarget)
}

val rogueSkill2 = Skill("Fan of Knives", 2, 2) { user, target ->
    val damage = user.atk - target.def
    val updatedTarget = target.copy(hp = max(0, target.hp - damage))
    user to updatedTarget

}

// Skills for the Healer class
val healerSkill1 = Skill("Heal", 2, 1) { user, target ->
    val healAmount = user.atk * 2
    val updatedTarget = target.copy(hp = min(target.hp + healAmount, 100))
    Pair(user, updatedTarget)
}

val healerSkill2 = Skill("Mass Heal", 2, 3) { user, target ->
    val healAmount = user.atk
    val updatedTarget = target.copy(hp = min(target.hp + healAmount, 100))
    Pair(user, updatedTarget)
}


fun CharacterClass.createSampleCharacter(name: String = "${this.name} Character", team: Team = Team.ALLY): TacticalCharacter {
    val id = when (this) {
       else -> CharacterId.DEFAULT
    }

    val skills = when (this) {
        CharacterClass.WARRIOR -> listOf(warriorSkill1, warriorSkill2)
        CharacterClass.MAGE -> listOf(mageSkill1, mageSkill2)
        CharacterClass.ROGUE -> listOf(rogueSkill1, rogueSkill2)
        CharacterClass.HEALER -> listOf(healerSkill1, healerSkill2)
    }

    return TacticalCharacter(
        name = name,
        id = id,
        charClass = this,
        skills = skills,
        team = team
    )
}

