package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Handgun(val world: World, val maxAmo: Int, private val myAssetManager: MyAssetManager) {

    val gunOffsetX = 1.15
    val gunOffsetY = -0.46

    val bulletsList = mutableListOf<Bullet>()

    private lateinit var flashPos: Vector2
    var mouseAngle: Float = 0.0f

    private var flashTextureRegion: TextureRegion = myAssetManager.flashTextureRegion!!

    private fun getFlashPos(playerPosition: Vector2, moueAngle: Double): Vector2 {
        val gunOffsetX = 1.15 * PLAYER_SCALE
        val gunOffsetY = -0.46 * PLAYER_SCALE

        val bulletX: Double = playerPosition.x + (gunOffsetX * cos(Math.toRadians(moueAngle)) - gunOffsetY * sin(Math.toRadians(moueAngle)))
        val bulletY: Double = playerPosition.y + (gunOffsetX * sin(Math.toRadians(moueAngle)) + gunOffsetY * cos(Math.toRadians(moueAngle)))

        return Vector2(bulletX.toFloat(), bulletY.toFloat())
    }

    fun update(playerPosition: Vector2, mouseAngle: Double) {
        flashPos = getFlashPos(playerPosition, mouseAngle)
        this.mouseAngle = mouseAngle.toFloat()
        bulletsList.forEach { bullet -> bullet.update() }
    }

    fun shoot(mousePos: Vector2) {
        flashTextureRegion.flip(false, Random(0).nextBoolean())
        bulletsList.add(Bullet(world, flashPos, mousePos, mouseAngle, 5f))
    }

    private fun getFlashSize(): Pair<Float, Float> {
        val width = flashTextureRegion.regionWidth * PPM * 0.15f
        val height = flashTextureRegion.regionHeight * PPM * 0.15f
        return Pair(width, height)
    }

    fun draw(batch: SpriteBatch) {
        batch.draw(flashTextureRegion, flashPos.x - getFlashSize().first / 2, flashPos.y - getFlashSize().second / 2, getFlashSize().first / 2, getFlashSize().second / 2, getFlashSize().first, getFlashSize().second, 1f, 1f, mouseAngle)
    }
}