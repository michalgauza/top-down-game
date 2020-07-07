package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion


class MyAssetManager {
    val assetManager = AssetManager()

    var playerIdleAtlas: TextureAtlas? = null
    var playerIdleAnimation: Animation<TextureRegion>? = null

    var playerShootAtlas: TextureAtlas? = null
    var playerShootAnimation: Animation<TextureRegion>? = null

    var playerReloadAtlas: TextureAtlas? = null
    var playerReloadAnimation: Animation<TextureRegion>? = null

    var playerMeleeAtlas: TextureAtlas? = null
    var playerMeleeAnimation: Animation<TextureRegion>? = null

    var playerFlashlightIdleAtlas: TextureAtlas? = null
    var playerFlashlightIdleAnimation: Animation<TextureRegion>? = null

    var playerFlashlightMeleeAtlas: TextureAtlas? = null
    var playerFlashlightMeleeAnimation: Animation<TextureRegion>? = null

    var playerKnifeIdleAtlas: TextureAtlas? = null
    var playerKnifeIdleAnimation: Animation<TextureRegion>? = null

    var playerKnifeMeleeAtlas: TextureAtlas? = null
    var playerKnifeMeleeAnimation: Animation<TextureRegion>? = null

    var flashTexture: Texture? = null
    var flashTextureRegion: TextureRegion? = null

    fun loadImages() {
        assetManager.load("player_idle.atlas", TextureAtlas::class.java)
        assetManager.load("player_shoot.atlas", TextureAtlas::class.java)
        assetManager.load("player_reload.atlas", TextureAtlas::class.java)
        assetManager.load("player_melee.atlas", TextureAtlas::class.java)
        assetManager.load("player_flashlight_idle.atlas", TextureAtlas::class.java)
        assetManager.load("player_flashlight_melee.atlas", TextureAtlas::class.java)
        assetManager.load("player_knife_idle.atlas", TextureAtlas::class.java)
        assetManager.load("player_knife_melee.atlas", TextureAtlas::class.java)
        assetManager.load("flash.png", Texture::class.java)
        assetManager.finishLoading()
        playerIdleAtlas = assetManager["player_idle.atlas"]
        playerShootAtlas = assetManager["player_shoot.atlas"]
        playerReloadAtlas = assetManager["player_reload.atlas"]
        playerMeleeAtlas = assetManager["player_melee.atlas"]
        playerFlashlightIdleAtlas = assetManager["player_flashlight_idle.atlas"]
        playerFlashlightMeleeAtlas = assetManager["player_flashlight_melee.atlas"]
        playerKnifeIdleAtlas = assetManager["player_knife_idle.atlas"]
        playerKnifeMeleeAtlas = assetManager["player_knife_melee.atlas"]
        flashTexture = assetManager["flash.png"]
        flashTextureRegion = TextureRegion(flashTexture)
        createAnimations()

    }

    fun createAnimations() {
        playerIdleAnimation = Animation<TextureRegion>(1 / 30f, playerIdleAtlas?.regions)
        playerShootAnimation = Animation<TextureRegion>(1 / 30f, playerShootAtlas?.regions)
        playerReloadAnimation = Animation<TextureRegion>(1 / 30f, playerReloadAtlas?.regions)
        playerMeleeAnimation = Animation<TextureRegion>(1 / 30f, playerMeleeAtlas?.regions)
        playerFlashlightIdleAnimation = Animation<TextureRegion>(1 / 30f, playerFlashlightIdleAtlas?.regions)
        playerFlashlightMeleeAnimation = Animation<TextureRegion>(1 / 30f, playerFlashlightMeleeAtlas?.regions)
        playerKnifeIdleAnimation = Animation<TextureRegion>(1 / 30f, playerKnifeIdleAtlas?.regions)
        playerKnifeMeleeAnimation = Animation<TextureRegion>(1 / 30f, playerKnifeMeleeAtlas?.regions)
    }

}