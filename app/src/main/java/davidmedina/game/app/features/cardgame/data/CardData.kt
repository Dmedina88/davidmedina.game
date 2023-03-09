package davidmedina.game.app.features.cardgame.data

import davidmedina.game.app.R

data class CardData(
    val name: String,
    val cost: Int,
    val life: Int,
    val imageId: Int,
    val actions: List<CardAction>,
    val lore: String = "lore"
)


data class CardState(val faceUp: Boolean = false, val cardData: CardData)



sealed class CardAction(val cost: Int) {
    data class Attack(val damage: Int) : CardAction(1)
    data class DirectAttack(val damage: Int) : CardAction(1)
    data class Heal(val damage: Int) : CardAction(1)
}


var mockCardState = CardState(
    true,
    CardData("Cavid", 3, 3, R.mipmap.cavis, listOf(CardAction.Attack(2), CardAction.Heal(1)))
)


/*

    listOf(
    CardData(
        name = "Shamanic Journey",
        cost = 3,
        life = 0,
        imageId = R.drawable.shamanic_journey,
        actions = listOf(
            CardAction("Draw 2 cards", ActionType.DRAW),
            CardAction("Add 1 mana crystal", ActionType.ADD_MANA)
        ),
        lore = "In ancient times, shamans would use hallucinogenic plants to connect with the spiritual realm and seek guidance from their ancestors."
    ),
    CardData(
        name = "Megalodon",
        cost = 7,
        life = 8,
        imageId = R.drawable.megalodon,
        actions = listOf(
            CardAction("Deal 4 damage to a random enemy minion", ActionType.DAMAGE),
            CardAction("Draw 1 card", ActionType.DRAW)
        ),
        lore = "The megalodon was a massive prehistoric shark that roamed the oceans millions of years ago. It was said to be the apex predator of its time."
    ),
    CardData(
        name = "Psychedelic Ritual",
        cost = 5,
        life = 0,
        imageId = R.drawable.psychedelic_ritual,
        actions = listOf(
            CardAction("Give all friendly minions +2 attack", ActionType.BUFF),
            CardAction("Deal 2 damage to all enemy minions", ActionType.AOE_DAMAGE)
        ),
        lore = "In some cultures, psychedelic substances were used in religious rituals to induce altered states of consciousness and connect with the divine."
    )
)
val cardDataList = listOf(
    CardData(
        "Triceratopsaurus",
        5,
        7,
        R.drawable.triceratops,
        listOf(
            CardAction("Charge", "Deal 2 damage to enemy creature"),
            CardAction("Stampede", "Deal 3 damage to enemy player")
        ),
        "Legend has it that these creatures once roamed the earth in massive herds."
    ),
    CardData(
        "Mushroom Shaman",
        3,
        4,
        R.drawable.mushroom,
        listOf(
            CardAction("Fungal Growth", "Heal all friendly creatures for 2 health"),
            CardAction("Spore Cloud", "Enemy creatures have -1 attack this turn")
        ),
        "This mystical shaman uses the power of psychedelic mushrooms to protect their tribe."
    ),
    CardData(
        "Saber-tooth Tiger",
        4,
        6,
        R.drawable.sabertooth,
        listOf(
            CardAction("Pounce", "Deal 3 damage to enemy creature"),
            CardAction("Ravage", "Deal 4 damage to enemy player")
        ),
        "This powerful predator was feared by humans and animals alike."
    ),
    CardData(
        "Dreamweaver",
        2,
        3,
        R.drawable.dreamcatcher,
        listOf(
            CardAction("Nightmare", "Choose an enemy creature. It has -2 attack this turn."),
            CardAction("Dream", "Choose a friendly creature. It gains +2 health this turn.")
        ),
        "This spiritual leader guides their people through the mystical realm of dreams."
    )
)


*/

