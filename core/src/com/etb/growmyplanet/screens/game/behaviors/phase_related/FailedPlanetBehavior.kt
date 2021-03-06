package com.etb.growmyplanet.screens.game.behaviors.phase_related

import com.badlogic.gdx.Gdx
import com.etb.growmyplanet.UseCase
import com.etb.growmyplanet.screens.game.FAILED_ANIMATION_FINISHED_USE_CASE
import com.etb.growmyplanet.screens.game.behaviors.PlanetBehavior
import com.etb.growmyplanet.screens.game.behaviors.phase_related.di.GamePhaseScope
import com.etb.growmyplanet.screens.game.models.PlanetModel
import com.etb.growmyplanet.screens.game.views.AnimationListener
import com.etb.growmyplanet.screens.game.views.Planet
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by etb on 26.08.2018.
 */
@GamePhaseScope
class FailedPlanetBehaviorFactory @Inject constructor(
        private val model: PlanetModel,
        @Named(FAILED_ANIMATION_FINISHED_USE_CASE)
        private val animationFinishedListener: UseCase<@JvmWildcard Unit, @JvmWildcard Unit>
) {
    fun createBehavior(failedReason: FailedReason): FailedPlanetBehavior {
        return FailedPlanetBehavior(model, failedReason, animationFinishedListener)
    }
}

class FailedPlanetBehavior(
        model: PlanetModel,
        private val failedReason: FailedReason,
        private val animationFinishedListener: UseCase<Unit, Unit>
): PlanetBehavior(model) {

    override fun attachView(view: Planet) {
        super.attachView(view)
        when(failedReason) {
            is Absorption -> {
                view.startAbsorbAnimation(
                        failedReason.absorptionTarget,
                        getListener()
                )
            }
            is Collision -> {
                view.startFallingAnimation(getListener())
            }
        }
    }

    private fun getListener(): AnimationListener {
        return object : AnimationListener {
            override fun onAnimationFinished() {
                Gdx.app.log("@", "onAnimationFinished")
                view?.hide()
                detachView()
                dispose()
                animationFinishedListener.invoke(Unit)
            }

        }
    }
}