package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.viewport.FitViewport
import kotlin.math.cos
import kotlin.math.sin

const val PLAYER_SCALE = 0.65f

enum class WEAPON {
    FLASHLIGHT, HANDGUN, KNIFE, RIFLE, SHOTGUN
}

class Player(private val viewport: FitViewport, private val world: World, private val myAssetManager: MyAssetManager) {

    var timer = 0f
    var currentAnimation: Animation<TextureRegion>

    var currentState = State.IDLE
    var previousState = currentState

    var flashTextureRegion: TextureRegion

    private lateinit var playerTextureRegion: TextureRegion

    lateinit var body: Body

    init {
        createBody()
        currentAnimation = myAssetManager.playerIdleAnimation!!
        flashTextureRegion = myAssetManager.flashTextureRegion!!
    }

    private fun createBody() {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(Vector2(1f, 1f))
        bodyDef.linearDamping = 5f
        val fixtureDef = FixtureDef()
        val shape = PolygonShape()
        shape.set(arrayOf(Vector2(-0.7f, 0.5f).scl(PLAYER_SCALE), Vector2(-0.1f, 0.5f).scl(PLAYER_SCALE), Vector2(0.7f, 0.2f).scl(PLAYER_SCALE), Vector2(0.6f, -0.5f).scl(PLAYER_SCALE), Vector2(-0.4f, -0.7f).scl(PLAYER_SCALE), Vector2(-0.9f, -0.5f).scl(PLAYER_SCALE)))
        fixtureDef.shape = shape
        body = world.createBody(bodyDef)
        body.createFixture(fixtureDef).userData = this
    }

    fun update(delta: Float) {
        movement()
        body.setTransform(body.position, Math.toRadians(getMouseAngle().toDouble()).toFloat())
        getAnimation(delta)
    }

    private fun movement() {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.applyForceToCenter(Vector2(0f, 30f), true)
            if (currentState != State.MELEE && currentState != State.RELOAD && currentState != State.SHOOT) {
                currentState = State.WALK
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            body.applyForceToCenter(Vector2(0f, -30f), true)
            if (currentState != State.MELEE && currentState != State.RELOAD && currentState != State.SHOOT) {
                currentState = State.WALK
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.applyForceToCenter(Vector2(30f, 0f), true)
            if (currentState != State.MELEE && currentState != State.RELOAD && currentState != State.SHOOT) {
                currentState = State.WALK
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.applyForceToCenter(Vector2(-30f, 0f), true)
            if (currentState != State.MELEE && currentState != State.RELOAD && currentState != State.SHOOT) {
                currentState = State.WALK
            }
        }

        when {
            Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) -> {
                currentState = State.MELEE
            }
            Gdx.input.isKeyPressed(Input.Keys.R) -> {
                if (currentState != State.MELEE) {
                    currentState = State.RELOAD
                }
            }
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) -> {
                if (currentState != State.MELEE && currentState != State.RELOAD) {
                    currentState = State.SHOOT
                }
            }
            else -> {
                currentAnimation = myAssetManager.playerIdleAnimation!!
            }
        }
    }

    private fun getAnimation(delta: Float) {
        if (currentState == previousState) timer += (delta * 0.5f) else timer = 0f

        if (currentState == State.MELEE) {
//            currentAnimation = myAssetManager.playerMeleeAnimation!!
            currentAnimation = myAssetManager.playerKnifeMeleeAnimation!!
//            currentAnimation = myAssetManager.playerFlashlightMeleeAnimation!!
            if (currentAnimation.isAnimationFinished(timer)) {
                currentState = State.IDLE
            }
        }
        if (currentState == State.RELOAD) {
            currentAnimation = myAssetManager.playerReloadAnimation!!
            if (currentAnimation.isAnimationFinished(timer)) {
                currentState = State.IDLE
            }
        }
        if (currentState == State.SHOOT) {
            currentAnimation = myAssetManager.playerShootAnimation!!
            if (currentAnimation.isAnimationFinished(timer)) {
                currentState = State.IDLE
            }
        }
        if (currentState == State.IDLE) {
//            currentAnimation = myAssetManager.playerIdleAnimation!!
            currentAnimation = myAssetManager.playerKnifeIdleAnimation!!
//            currentAnimation = myAssetManager.playerFlashlightIdleAnimation!!
        }

        previousState = currentState
    }

    private fun getMouseAngle(): Float {
        val mousePos = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.camera.unproject(mousePos)
        val angle = Vector2(mousePos.x, mousePos.y).sub(body.position).angleRad()
        return Math.toDegrees(angle.toDouble()).toFloat()
    }

    private fun getFlashPos(): Vector2 {
        val gunOffsetX = 1.2 * PLAYER_SCALE
        val gunOffsetY = -0.46 * PLAYER_SCALE

        val bulletX: Double = body.position.x + (gunOffsetX * cos(Math.toRadians(getMouseAngle().toDouble())) - gunOffsetY * sin(Math.toRadians(getMouseAngle().toDouble())))
        val bulletY: Double = body.position.y + (gunOffsetX * sin(Math.toRadians(getMouseAngle().toDouble()))) + gunOffsetY * cos(Math.toRadians(getMouseAngle().toDouble()))

        return Vector2(bulletX.toFloat(), bulletY.toFloat())
    }

    private fun getFlashSize(): Pair<Float, Float> {
        val width = flashTextureRegion.regionWidth * PPM * 0.15f
        val height = flashTextureRegion.regionHeight * PPM * 0.15f
        return Pair(width, height)
    }

    private fun getPlayerSize(): Pair<Float, Float> {
        val width = playerTextureRegion.regionWidth * PPM * PLAYER_SCALE
        val height = playerTextureRegion.regionHeight * PPM * PLAYER_SCALE
        return Pair(width, height)
    }

    fun draw(batch: SpriteBatch) {
        getFlashPos()
        if (currentState == State.SHOOT) {
            batch.draw(flashTextureRegion, getFlashPos().x - getFlashSize().first / 2, getFlashPos().y - getFlashSize().second / 2, getFlashSize().first / 2, getFlashSize().second / 2, getFlashSize().first, getFlashSize().second, 1f, 1f, getMouseAngle())
        }
        playerTextureRegion = currentAnimation.getKeyFrame(timer, true)
        batch.draw(playerTextureRegion, body.position.x - getPlayerSize().first / 2, body.position.y - getPlayerSize().second / 2, getPlayerSize().first / 2, getPlayerSize().second / 2, getPlayerSize().first, getPlayerSize().second, 1f, 1f, getMouseAngle())
    }
}