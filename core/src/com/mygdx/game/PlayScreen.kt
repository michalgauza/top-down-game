package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import java.awt.geom.RectangularShape

class PlayScreen(private val batch: SpriteBatch) : Screen {

    val camera = OrthographicCamera()
    val viewport = FitViewport(1280f * PPM, 720f * PPM, camera)

    val world = World(Vector2(0f,0f), false)
    val render = Box2DDebugRenderer()

    private val myAssetManager = MyAssetManager()

    lateinit var player: Player

    override fun show() {
        viewport.apply()
        myAssetManager.loadImages()
        player = Player(viewport, world, myAssetManager)
    }

    override fun render(delta: Float) {
        update(delta)
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.projectionMatrix = camera.combined
        batch.begin()
        player.draw(batch)
//        batch.draw(Texture(Gdx.files.internal("debug.png")), 0f, 0f, viewport.worldWidth / 2, viewport.worldHeight)
        batch.end()

        render.render(world, camera.combined)
    }

    private fun update(delta: Float) {
        world.step(1/60f, 6, 2)
        player.update(delta)
        camera.update()
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        myAssetManager.assetManager.dispose()
        world.dispose()
        render.dispose()
    }
}