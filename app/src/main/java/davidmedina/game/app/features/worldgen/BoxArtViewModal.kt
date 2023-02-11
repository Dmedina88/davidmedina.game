package davidmedina.game.app.features.worldgen

import androidx.lifecycle.ViewModel
import davidmedina.game.app.data.models.ArtGenAsset
import timber.log.Timber

class BoxArtViewModal : ViewModel() {

   fun onAssetClicked(artGenAsset: ArtGenAsset){

       when(artGenAsset){
           is ArtGenAsset.Flower-> {Timber.d(artGenAsset.toString())}
           is ArtGenAsset.BackGround -> TODO()
           is ArtGenAsset.Center -> TODO()
           is ArtGenAsset.Clouds -> TODO()
           is ArtGenAsset.Flying -> TODO()
           is ArtGenAsset.Ground -> TODO()
           is ArtGenAsset.GroundCreature -> TODO()
           is ArtGenAsset.LandTrait -> TODO()
           is ArtGenAsset.Scatter -> TODO()
           is ArtGenAsset.Sky -> TODO()
           is ArtGenAsset.Structures -> TODO()
       }

   }
}