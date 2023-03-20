package davidmedina.game.app.features.ai_musemum.tactical

import davidmedina.game.app.R


enum class CharacterClass {
    WARRIOR,
    MAGE,
    ROGUE,
    HEALER
}

enum class CharacterId {
    // Define the possible values for the character ids
    ALICE,
    BOB,
    DEFAULT
}

enum class Team {
    ALLY,
    ENEMY
}

fun CharacterClass.getSkillList(): List<Skill> {
    return when (this) {
        CharacterClass.WARRIOR -> listOf(warriorSkill1, warriorSkill2)
        CharacterClass.MAGE -> listOf(mageSkill1, mageSkill2)
        CharacterClass.ROGUE -> listOf(rogueSkill1, rogueSkill2)
        CharacterClass.HEALER -> listOf(healerSkill1, healerSkill2)
    }
}


data class TacticalCharacter(
    val name: String,
    val id: CharacterId,
    val team: Team,
    val level: Int = 1,
    val hp: Int = 100,
    val atk: Int = 10,
    val def: Int = 10,
    val spd: Int = 10,
    val charClass: CharacterClass = CharacterClass.WARRIOR,
    val skills: List<Skill> = charClass.getSkillList()
){
    // The rest of the methods are unchanged from before
    // A function to level up a character and return a new copy with increased stats
    fun levelUp(character: TacticalCharacter): TacticalCharacter {
        // Increase the level by one
        val newLevel = character.level + 1
        println("${character.name} levels up to $newLevel.")
        // Generate random numbers between 1 and 5 for each stat increase
        val hpIncrease = (1..5).random()
        val atkIncrease = (1..5).random()
        val defIncrease = (1..5).random()
        val spdIncrease = (1..5).random()
        // Increase each stat by the corresponding amount and print a message
        val newHp = character.hp + hpIncrease
        println("${character.name}'s HP increases by $hpIncrease.")
        val newAtk = character.atk + atkIncrease
        println("${character.name}'s ATK increases by $atkIncrease.")
        val newDef = character.def + defIncrease
        println("${character.name}'s DEF increases by $defIncrease.")
        val newSpd = character.spd + spdIncrease
        println("${character.name}'s SPD increases by $spdIncrease.")
        // Create and return a new copy of the character with the increased stats
        return character.copy(
            level = newLevel,
            hp = newHp,
            atk = newAtk,
            def = newDef,
            spd = newSpd
        )
    }

    // A method to check if the character is alive
    fun isAlive(): Boolean {
        return hp > 0
    }

    // A function to attack another character and deal damage based on their stats
    // A function to attack another character and deal damage based on their stats
    fun attack(target: TacticalCharacter): Pair<TacticalCharacter, TacticalCharacter> {
        // Calculate the damage as the difference between the attacker's attack and the target's defense
        val damage = this.atk - target.def
        // Declare a variable for the new target's health
        val newTargetHp: Int
        // If the damage is positive, reduce the target's health by that amount and print a message
        if (damage > 0) {
            newTargetHp = target.hp - damage
            println("${this.name} attacks ${target.name} for $damage damage.")
        } else {
            // If the damage is zero or negative, print a message that the attack has no effect and keep the target's health unchanged
            newTargetHp = target.hp
            println("${this.name}'s attack has no effect on ${target.name}.")
        }
        // print a message if the target is defeated
        if (newTargetHp == 0) {
            println("${target.name} is defeated.")
        }
        // Use the maxOf function to take the higher value of newTargetHp or 0 and
        // Create and return a new copy of this character and a new copy of the target with their updated health values
        return this.copy(hp = this.hp) to target.copy(hp = maxOf(newTargetHp, 0))
    }

    // A function to use a skill on another character and apply its effect
    fun useSkill(skill: Skill, target: TacticalCharacter): Pair<TacticalCharacter, TacticalCharacter> {
        // Print a message that the character uses the skill on the target
        println("${this.name} uses ${skill.name} on ${target.name}.")
        // Invoke the effect function of the skill with this character as the user and the target as the parameter and store their new copies in variables
        val (newUser, newTarget) = skill.effect(this, target)
        // Return a pair of newUser and newTarget as their updated copies
        return Pair(newUser, newTarget)
    }

    // A function to heal another character and restore their health based on their level
    fun heal(target: TacticalCharacter): Pair<TacticalCharacter, TacticalCharacter> {
        // Calculate the healing amount as 10 times this character's level
        // Calculate the healing amount as 10 times this character's level
        val healing = this.level * 10
        // Increase the target's health by that amount and print a message
        //  target.hp += healing
        println("${this.name} heals ${target.name} for $healing HP.")
        // If the target's health exceeds their maximum health, set it to their maximum health and print a message
        return Pair(this, target)

    }
}

// An extension function to map character ids to an image resource
fun TacticalCharacter.toImageResource(): Int {
    // Use a when expression to return the corresponding resource id based on the character id
    return when (this.id) {
        CharacterId.ALICE -> R.drawable.blue_oger
        CharacterId.BOB -> R.drawable.gen_center_12
        CharacterId.DEFAULT -> R.drawable.gen_center_16
    }
}
