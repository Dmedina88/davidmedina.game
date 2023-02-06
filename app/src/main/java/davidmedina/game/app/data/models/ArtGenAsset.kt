package davidmedina.game.app.data.models

import androidx.compose.ui.graphics.ImageBitmap
import davidmedina.game.app.R

sealed class ArtGenAsset(val bitmap: ImageBitmap) {

    class BackGround(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class Sky(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class Structures(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class Clouds(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class Flying(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class Ground(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class GroundCreature(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class LandTrait(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class Center(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class Scatter(bitmap: ImageBitmap) : ArtGenAsset(bitmap)
    class Flower(bitmap: ImageBitmap) : ArtGenAsset(bitmap)

}

val backgrounds = listOf(
    R.drawable.gen_background_1,
    R.drawable.gen_background_2,
    R.drawable.gen_background_3,
    R.drawable.gen_background_4,
    R.drawable.gen_background_5,
    R.drawable.gen_background_6,
    R.drawable.gen_background_7,
    R.drawable.gen_background_8,
    R.drawable.gen_background_9,
    R.drawable.gen_background_10,
)

val center = listOf(
    R.drawable.gen_center_1,
    R.drawable.gen_center_2,
    R.drawable.gen_center_3,
    R.drawable.gen_center_4,
    R.drawable.gen_center_5,
    R.drawable.gen_center_6,
    R.drawable.gen_center_7,
    R.drawable.gen_center_8,
    R.drawable.gen_center_9,
    R.drawable.gen_center_10,
    R.drawable.gen_center_11,
    R.drawable.gen_center_12,
    R.drawable.gen_center_13,
    R.drawable.gen_center_14,
    R.drawable.gen_center_15,
    R.drawable.gen_center_16,
    R.drawable.gen_center_17,
    R.drawable.gen_center_18,
    R.drawable.gen_center_19,
    R.drawable.gen_center_20,
    R.drawable.gen_center_21,
    R.drawable.gen_center_22,
    R.drawable.gen_center_23,
    R.drawable.gen_center_24,
    R.drawable.gen_center_25,
    R.drawable.gen_center_26,
    R.drawable.gen_center_27,
)

val clouds = listOf(
    R.drawable.gen_clouds_1,
    R.drawable.gen_clouds_2,
    R.drawable.gen_clouds_3,
    R.drawable.gen_clouds_4,
    R.drawable.gen_clouds_5,
    R.drawable.gen_clouds_6,
    R.drawable.gen_clouds_7,
    R.drawable.gen_clouds_8,
    R.drawable.gen_clouds_9,
    R.drawable.gen_clouds_10,
    R.drawable.gen_clouds_11,
    R.drawable.gen_clouds_12
)


val creature = listOf(
    R.drawable.gen_creature_11,
    R.drawable.gen_creature_12,
    R.drawable.gen_creature_13,
    R.drawable.gen_creature_14,
    R.drawable.gen_creature_15,
    R.drawable.gen_creature_16,
    R.drawable.gen_creature_17,
    R.drawable.gen_creature_18,
    R.drawable.gen_creature_19,
    R.drawable.gen_creature_110,
    R.drawable.gen_creature_111,
    R.drawable.gen_creature_112,
    R.drawable.gen_creature_113,
)

val flowers = listOf(
    R.drawable.gen_flower_1,
    R.drawable.gen_flower_2,
    R.drawable.gen_flower_4,
    R.drawable.gen_flower_6,
    R.drawable.gen_flower_7,
    R.drawable.gen_flower_8,
    R.drawable.gen_flower_9,
    R.drawable.gen_flower_10,
    R.drawable.gen_flower_11,
    R.drawable.gen_flower_12
)

val flying = listOf(
    R.drawable.gen_flying_11,
    R.drawable.gen_flying_110,
    R.drawable.gen_flying_111,
    R.drawable.gen_flying_112,
    R.drawable.gen_flying_113,
    R.drawable.gen_flying_114,
    R.drawable.gen_flying_115,
    R.drawable.gen_flying_116,
    R.drawable.gen_flying_12,
    R.drawable.gen_flying_13,
    R.drawable.gen_flying_14,
    R.drawable.gen_flying_15,
    R.drawable.gen_flying_16,
    R.drawable.gen_flying_17,
    R.drawable.gen_flying_18,
    R.drawable.gen_flying_19,
    )

val ground = listOf(
    R.drawable.gen_ground_1,
    R.drawable.gen_ground_2,
    R.drawable.gen_ground_3,
    R.drawable.gen_ground_4,
    R.drawable.gen_ground_5)

val landTrait = listOf(
    R.drawable.gen_land_trait_big_mushroom,
    R.drawable.gen_land_trait_12,
    R.drawable.gen_land_trait_13,
    R.drawable.gen_land_trait_14,
    R.drawable.gen_land_trait_15,
    R.drawable.gen_land_trait_15
)

val scatter = listOf(
    R.drawable.gen_scater_1,
    R.drawable.gen_scater_2,
    R.drawable.gen_scater_3,
    R.drawable.gen_scater_4,
    R.drawable.gen_scater_5,
    R.drawable.gen_scater_6,
    R.drawable.gen_scater_7,
    R.drawable.gen_scater_8,
    R.drawable.gen_scater_9,
    R.drawable.gen_scater_10,
    R.drawable.gen_scater_11,
    R.drawable.gen_scater_12,
    R.drawable.gen_scater_13,
    R.drawable.gen_scater_14,
    R.drawable.gen_scater_15,
    R.drawable.gen_scater_16,
    R.drawable.gen_scater_17,
    R.drawable.gen_scater_18,
    R.drawable.gen_scater_19,
    R.drawable.gen_scater_20)

val sky = listOf(
    R.drawable.gen_sky_1,
    R.drawable.gen_sky_2,
    R.drawable.gen_sky_3,
    R.drawable.gen_sky_4,
    R.drawable.gen_sky_5,
    R.drawable.gen_sky_6,
    R.drawable.gen_sky_7,
    R.drawable.gen_sky_8
)

val structures = listOf(
    R.drawable.gen_structure_1,
    R.drawable.gen_structure_2,
    R.drawable.gen_structure_3,
    R.drawable.gen_structure_4,
    R.drawable.gen_structure_5,
    R.drawable.gen_structure_6,
    R.drawable.gen_structure_7,
    R.drawable.gen_structure_green_fort,
    R.drawable.gen_structure_9,
    R.drawable.gen_structure_10,
    R.drawable.gen_structure_11,
    R.drawable.gen_structure_13
)