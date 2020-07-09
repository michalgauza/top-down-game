package com.mygdx.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*

class Bullet(private val world: World, private val spawnPos: Vector2, private val targetPos: Vector2, val angle: Float, val velocity: Float) {

    private lateinit var body: Body

    init {
        createBody()
    }

    private fun createBody() {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(spawnPos)
        }
        val direction = Vector2(targetPos.x - spawnPos.x, targetPos.y - spawnPos.y)
        bodyDef.linearVelocity.set(direction).nor().scl(velocity)
        val fixtureDef = FixtureDef()
        val shape = CircleShape().apply {
            radius = 0.5f
        }
        fixtureDef.shape = shape
        body = world.createBody(bodyDef).also { it.createFixture(fixtureDef).userData = this }
    }

    fun update(){
        body.linearVelocity.set(Vector2(5f, 5f))
    }
}