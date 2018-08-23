package com.yarrtest.balloon.screens.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.yarrtest.balloon.BalloonGame
import com.yarrtest.balloon.base.AppConfig
import com.yarrtest.balloon.base.BaseScreen
import com.yarrtest.balloon.screens.di.ScreenScope
import com.yarrtest.balloon.screens.game.di.ControllersProvider
import com.yarrtest.balloon.screens.game.di.GameScreenComponent
import com.yarrtest.balloon.screens.game.views.Balloon
import com.yarrtest.balloon.screens.game.views.Ring
import dagger.Module
import dagger.Provides
import javax.inject.Inject

/**
 * Created by etb on 21.08.2018.
 */

private const val TAG = "com.yarrtest.balloon.screens.game.GameScreen"

@Module
class GameScreen(
        game: BalloonGame,
        config: AppConfig
) : BaseScreen<BalloonGame, FitViewport>(
        game,
        FitViewport(config.camWidth, config.camHeight)
) {

    var component: GameScreenComponent? = null
    private set

    @Inject lateinit var controller: GameController

    private var isShown = false
    private val layers = mutableMapOf<Layer, Group>()

    private val layerHandler = object : LayerHandler {
        override fun addActorOnLayer(actor: Actor, layer: Layer) {
            layers[layer]?.addActor(actor)
                    ?: Gdx.app.error(TAG, "Can't find group for layer: $layer")
        }
    }

    init {
        component = game.component!!.plus(
                this,
                ControllersProvider()
        ).also {
            it.inject(this)
        }
        lifecycleListener = controller
        addInputListener(controller)
    }


    override fun prepareStage(stage: Stage) {
        if (isShown.not()) {
            //TODO: show background
            controller.loadLevel(this, 0)
            isShown = true
        }
    }

    override fun resume() {
        super.resume()
        addLayerGroups()
    }

    override fun pause() {
        super.pause()
    }

    override fun render(delta: Float) {
        super.render(delta)
        controller.update(delta)
    }

    @ScreenScope
    @Provides
    fun createBalloonView(): Balloon {
        val texture = Texture(Gdx.files.internal("ball.png"))
        return Balloon(layerHandler, texture)
    }

    @ScreenScope
    @Provides
    fun createRingView(): Ring {
        val texture = Texture(Gdx.files.internal("net.png"))
        return Ring(layerHandler, texture, texture)
    }

    private fun addLayerGroups() {
        Layer.values().forEach {
            layers[it] = Group().also {
                stage?.addActor(it)
            }
        }
    }
}