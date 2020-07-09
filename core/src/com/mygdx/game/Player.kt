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
import kotlin.random.Random

const val PLAYER_SCALE = 0.65f
const val PLAYER_VELOCITY = 30f

enum class WEAPON {
    FLASHLIGHT, HANDGUN, KNIFE, RIFLE, SHOTGUN
}

class Player(private val viewport: FitViewport, private val world: World, private val myAssetManager: MyAssetManager) {

    private val handgun = Handgun(world, 9, myAssetManager)

    private var weapon = WEAPON.HANDGUN

    private var timer = 0f
    private var animation: Animation<TextureRegion>

    var currentState = State.IDLE
    var previousState = currentState


    private lateinit var playerTextureRegion: TextureRegion

    lateinit var body: Body

    init {
        createBody()
        animation = myAssetManager.playerIdleAnimation!!
    }

    private fun createBody() {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(Vector2(1f, 1f))
            linearDamping = 5f
        }
        val fixtureDef = FixtureDef()
        val shape = PolygonShape().apply {
            set(arrayOf(Vector2(-0.7f, 0.5f).scl(PLAYER_SCALE), Vector2(-0.1f, 0.5f).scl(PLAYER_SCALE), Vector2(0.7f, 0.2f).scl(PLAYER_SCALE), Vector2(0.6f, -0.5f).scl(PLAYER_SCALE), Vector2(-0.4f, -0.7f).scl(PLAYER_SCALE), Vector2(-0.9f, -0.5f).scl(PLAYER_SCALE)))
        }
        fixtureDef.shape = shape
        body = world.createBody(bodyDef).also { it.createFixture(fixtureDef).userData = this }
    }

    fun update(delta: Float) {
        movement()
        interactions()
        body.setTransform(body.position, Math.toRadians(getMouseAngle().toDouble()).toFloat())
        getAnimation(delta)

        handgun.update(body.position, getMouseAngle().toDouble())
    }

    private fun movement() {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.applyForceToCenter(Vector2(0f, PLAYER_VELOCITY), true)
            if (currentState != State.MELEE && currentState != State.RELOAD && currentState != State.SHOOT) {
                currentState = State.WALK
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            body.applyForceToCenter(Vector2(0f, -PLAYER_VELOCITY), true)
            if (currentState != State.MELEE && currentState != State.RELOAD && currentState != State.SHOOT) {
                currentState = State.WALK
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.applyForceToCenter(Vector2(PLAYER_VELOCITY, 0f), true)
            if (currentState != State.MELEE && currentState != State.RELOAD && currentState != State.SHOOT) {
                currentState = State.WALK
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.applyForceToCenter(Vector2(-PLAYER_VELOCITY, 0f), true)
            if (currentState != State.MELEE && currentState != State.RELOAD && currentState != State.SHOOT) {
                currentState = State.WALK
            }
        }
    }

    private fun interactions() {
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
                    handgun.shoot(getMousePos())
                    currentState = State.SHOOT
                }
            }
            else -> {
                animation = myAssetManager.playerIdleAnimation!!
            }
        }
    }

    private fun getAnimation(delta: Float) {
        if (currentState == previousState) timer += (delta * 0.5f) else timer = 0f

        if (currentState == State.MELEE) {
            animation = myAssetManager.playerMeleeAnimation!!
//            currentAnimation = myAssetManager.playerKnifeMeleeAnimation!!
//            currentAnimation = myAssetManager.playerFlashlightMeleeAnimation!!
            if (animation.isAnimationFinished(timer)) {
                currentState = State.IDLE
            }
        }
        if (currentState == State.RELOAD) {
            animation = myAssetManager.playerReloadAnimation!!
            if (animation.isAnimationFinished(timer)) {
                currentState = State.IDLE
            }
        }
        if (currentState == State.SHOOT) {
            animation = myAssetManager.playerShootAnimation!!
            if (animation.isAnimationFinished(timer)) {
                currentState = State.IDLE
            }
        }
        if (currentState == State.IDLE) {
            animation = myAssetManager.playerIdleAnimation!!
//            currentAnimation = myAssetManager.playerKnifeIdleAnimation!!
//            currentAnimation = myAssetManager.playerFlashlightIdleAnimation!!
        }

        previousState = currentState
    }

    private fun getMouseAngle(): Float {
        val mousePos = getMousePos()
        val angle = Vector2(mousePos.x, mousePos.y).sub(body.position).angleRad()
        return Math.toDegrees(angle.toDouble()).toFloat()
    }

    private fun getPlayerSize(): Pair<Float, Float> {
        val width = playerTextureRegion.regionWidth * PPM * PLAYER_SCALE
        val height = playerTextureRegion.regionHeight * PPM * PLAYER_SCALE
        return Pair(width, height)
    }

    private fun getMousePos(): Vector2 {
        val mousePos = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.camera.unproject(mousePos)
        return Vector2(mousePos.x , mousePos.y)
    }

    fun draw(batch: SpriteBatch) {
        playerTextureRegion = animation.getKeyFrame(timer, true)
        if (currentState == State.SHOOT) {
            handgun.draw(batch)
        }
        batch.draw(playerTextureRegion, body.position.x - getPlayerSize().first / 2, body.position.y - getPlayerSize().second / 2, getPlayerSize().first / 2, getPlayerSize().second / 2, getPlayerSize().first, getPlayerSize().second, 1f, 1f, getMouseAngle())
    }
}