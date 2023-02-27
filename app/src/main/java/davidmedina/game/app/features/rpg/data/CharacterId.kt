package davidmedina.game.app.features.rpg.data

import davidmedina.game.app.R

enum class CharacterId {
    BLUE_OGER,
    OTHER_OGER,
    Berserker,
    Wizard,
    Cyborg,
    Alien,
    Android,
    Psion,
    Minion,
    Dreamer
}

fun CharacterId.createCharacter(name: String? = null) = when (this) {
    CharacterId.BLUE_OGER -> Character(
        name ?: "BlueOger",
        CharacterId.BLUE_OGER,
        DiminishableStates(50, 50),
        DiminishableStates(50, 50),
        16,
        10,
        .03f,
        10
    )

    CharacterId.OTHER_OGER -> Character(
        name ?: "Othger",
        CharacterId.OTHER_OGER,
        DiminishableStates(30, 30),
        DiminishableStates(50, 50),
        10,
        10,
        .03f,
        10
    )

    CharacterId.Berserker -> Character(
        name ?: "Berserker",
        CharacterId.Berserker,
        DiminishableStates(60, 60),
        DiminishableStates(40, 40),
        20,
        5,
        .05f,
        5
    )

    CharacterId.Wizard -> Character(
        name ?: "Wizard",
        CharacterId.Wizard,
        DiminishableStates(45, 45),
        DiminishableStates(55, 55),
        5,
        5,
        .1f,
        20
    )

    CharacterId.Cyborg -> Character(
        name ?: "Cyborg",
        CharacterId.Cyborg,
        DiminishableStates(55, 55),
        DiminishableStates(45, 45),
        15,
        10,
        .04f,
        10
    )


    CharacterId.Android -> Character(
        name ?: "Android",
        CharacterId.Android,
        DiminishableStates(50, 50),
        DiminishableStates(40, 40),
        12,
        12,
        .07f,
        15
    )

    CharacterId.Alien -> Character(
        name ?: "Alien",
        CharacterId.Alien,
        DiminishableStates(45, 45),
        DiminishableStates(55, 55),
        8,
        8,
        .12f,
        25
    )

    CharacterId.Psion -> Character(
        name ?: "Psion",
        CharacterId.Psion,
        DiminishableStates(40, 40),
        DiminishableStates(60, 60),
        3,
        3,
        .15f,
        30
    )

    CharacterId.Minion -> Character(
        name ?: "Minion",
        CharacterId.Minion,
        DiminishableStates(35, 35),
        DiminishableStates(35, 35),
        1,
        1,
        .01f,
        1
    )

    CharacterId.Dreamer -> Character(
        name ?: "Dreamer",
        CharacterId.Dreamer,
        DiminishableStates(40, 40),
        DiminishableStates(50, 50),
        5,
        5,
        .09f,
        10
    )
}


fun Character.levelUp(newExp: Int): Character {
    val targetXP = exp + newExp
    return if (targetXP >= nextLevel) {
        levelUpCharacter(targetXP - nextLevel).levelUp(0)
    } else {
        this.copy(exp = targetXP)
    }
}


private fun Character.levelUpCharacter(newExp: Int): Character {
    return when (characterID) {
        CharacterId.BLUE_OGER, CharacterId.OTHER_OGER -> {
            this.copy(
                hp = hp.increaseMax(15),
                will = will.increaseMax(15),
                strength = strength + 3,
                defense = defense + 3,
                speed = speed + 0.2f,
                mind = mind + 2,
                exp = newExp,
                level = level + 1
            )
        }
        CharacterId.Berserker -> {
            this.copy(
                hp = hp.increaseMax(20),
                will = will.increaseMax(5),
                strength = strength + 4,
                defense = defense + 2,
                speed = speed + 0.1f,
                mind = mind + 1,
                exp = newExp,
                level = level + 1
            )
        }
        CharacterId.Wizard -> {
            this.copy(
                hp = hp.increaseMax(5),
                will = will.increaseMax(25),
                strength = strength + 1,
                defense = defense + 2,
                speed = speed + 0.1f,
                mind = mind + 4,
                exp = newExp,
                level = level + 1
            )
        }
        CharacterId.Cyborg -> {
            this.copy(
                hp = hp.increaseMax(15),
                will = will.increaseMax(10),
                strength = strength + 2,
                defense = defense + 3,
                speed = speed + 0.2f,
                mind = mind + 2,
                exp = newExp,
                level = level + 1
            )
        }
        CharacterId.Alien -> {
            this.copy(
                hp = hp.increaseMax(10),
                will = will.increaseMax(15),
                strength = strength + 1,
                defense = defense + 2,
                speed = speed + 0.2f,
                mind = mind + 3,
                exp = newExp,
                level = level + 1
            )
        }
        CharacterId.Android -> {
            this.copy(
                hp = hp.increaseMax(10),
                will = will.increaseMax(5),
                strength = strength + 2,
                defense = defense + 3,
                speed = speed + 0.2f,
                mind = mind + 2,
                exp = newExp,
                level = level + 1
            )
        }
        CharacterId.Psion -> {
            this.copy(
                hp = hp.increaseMax(5),
                will = will.increaseMax(30),
                strength = strength + 1,
                defense = defense + 2,
                speed = speed + 0.1f,
                mind = mind + 5,
                exp = newExp,
                level = level + 1
            )
        }
        CharacterId.Minion -> {
            this.copy(
                hp = hp.increaseMax(5),
                will = will.increaseMax(5),
                strength = strength + 1,
                defense = defense + 1,
                speed = speed + 0.2f,
                mind = mind + 1,
                exp = newExp,
                level = level + 1
            )
        }
        CharacterId.Dreamer -> {
            this.copy(
                hp = hp.increaseMax(5),
                will = will.increaseMax(20),
                strength = strength + 1,
                defense = defense + 1,
                speed = speed + 0.1f,
                mind = mind + 4,
                exp = newExp,
                level = level + 1
            )
        }
    }
}

val CharacterId.battleImage: Int
    get() = when (this) {
        CharacterId.BLUE_OGER -> R.drawable.blue_oger_portrite
        CharacterId.OTHER_OGER -> R.drawable.other_oger
        else -> {
            R.drawable.gen_land_trait_apple_tree
        }
    }
