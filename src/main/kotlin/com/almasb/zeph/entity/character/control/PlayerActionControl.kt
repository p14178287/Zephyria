package com.almasb.zeph.entity.character.control

import com.almasb.fxgl.ai.pathfinding.AStarGrid
import com.almasb.fxgl.ecs.AbstractControl
import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.app.FXGL
import com.almasb.zeph.ZephyriaApp
import com.almasb.zeph.entity.ai.AttackControl
import com.almasb.zeph.entity.ai.MovementControl
import com.almasb.zeph.entity.character.CharacterEntity
import com.almasb.zeph.entity.character.PlayerEntity
import javafx.beans.property.SimpleObjectProperty

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PlayerActionControl : AbstractControl() {

    private lateinit var player: PlayerEntity

    val moveControl = MovementControl()
    val attackControl = AttackControl()

    val selected = SimpleObjectProperty<Entity>()

    private var attacking = false

    private val grid: AStarGrid

    init {
        grid = (FXGL.getApp() as ZephyriaApp).grid
    }

    override fun onAdded(entity: Entity) {
        player = entity as PlayerEntity

        attackControl.enabled = false
        attackControl.selected.bind(selected)

        player.addControl(moveControl)
        player.addControl(attackControl)
    }

    override fun onUpdate(entity: Entity, tpf: Double) {

        if (selected.value is CharacterEntity /* TODO: && can be attacked, i.e. enemy */) {

            if (player.isInWeaponRange(selected.value as CharacterEntity)) {
                if (!attacking) {
                    moveControl.enabled = false
                    attackControl.enabled = true
                    attacking = true
                }
            } else {
                val target = selected.value as CharacterEntity
                moveTo(target.getTileX(), target.getTileY())
            }

        } else {
            if (attacking) {
                moveControl.enabled = true
                attackControl.enabled = false
                attacking = false
            }
        }
    }

    fun moveTo(x: Int, y: Int) {
        moveControl.moveTo(x, y)
    }
}