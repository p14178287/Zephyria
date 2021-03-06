package com.almasb.zeph.entity

import com.almasb.fxgl.ecs.Component
import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.entity.EntityView
import com.almasb.fxgl.ui.ProgressBar
import com.almasb.zeph.Config
import com.almasb.zeph.entity.character.CharacterEntity
import com.almasb.zeph.entity.character.component.SubViewComponent
import com.almasb.zeph.entity.item.ArmorEntity
import com.almasb.zeph.entity.item.WeaponEntity
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import java.lang.reflect.Method
import java.util.*

@Suppress("UNCHECKED_CAST")
/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object EntityManager {

    private val weapons = HashMap<Int, Method>()
    private val armor = HashMap<Int, Method>()

    fun getWeapon(id: Int) = WeaponEntity(weapons[id]!!.invoke(Data.Weapon) as List<Component>)

    fun getArmor(id: Int) = ArmorEntity(armor[id]!!.invoke(Data.Armor) as List<Component>)

    fun getItem(id: Int): Entity {
        if (weapons.containsKey(id))
            return getWeapon(id)

        if (armor.containsKey(id))
            return getArmor(id)

        throw IllegalArgumentException("ID $id not found in the database")
    }

    init {
        Data.Weapon.javaClass.declaredMethods.forEach {
            val list = it.invoke(Data.Weapon) as List<Component>

            val id = (list[0] as DescriptionComponent).id.value
            weapons[id] = it
        }

        Data.Armor.javaClass.declaredMethods.forEach {
            val list = it.invoke(Data.Armor) as List<Component>

            val id = (list[0] as DescriptionComponent).id.value
            armor[id] = it
        }
    }

    fun makeHPBar(): ProgressBar {
        val bar = ProgressBar(false)

        with(bar) {
            setHeight(25.0)
            setFill(Color.GREEN.brighter())
            setTraceFill(Color.GREEN.brighter())
            isLabelVisible = true
        }

        return bar
    }

    fun makeSkillBar(): ProgressBar {
        val bar = ProgressBar(false)

        with(bar) {
            setHeight(25.0)
            setFill(Color.BLUE.brighter().brighter())
            setTraceFill(Color.BLUE)
            isLabelVisible = true
        }

        return bar
    }

    fun makeCharacterSubView(entity: CharacterEntity): SubViewComponent {
        val barHP = makeHPBar()
        val barSP = makeSkillBar()

        barHP.translateX = 0.0
        barHP.translateY = 80.0
        barHP.setWidth(Config.tileSize.toDouble())
        barHP.setHeight(10.0)
        barHP.isLabelVisible = false

        barSP.translateX = 0.0
        barSP.translateY = 90.0
        barSP.setWidth(Config.tileSize.toDouble())
        barSP.setHeight(10.0)
        barSP.isLabelVisible = false

        barHP.maxValueProperty().bind(entity.hp.maxValueProperty())
        barHP.currentValueProperty().bind(entity.hp.valueProperty())

        barSP.maxValueProperty().bind(entity.sp.maxValueProperty())
        barSP.currentValueProperty().bind(entity.sp.valueProperty())

        val text = Text()
        text.font = Font.font(14.0)
        text.fill = Color.WHITE
        text.textProperty().bind(entity.description.name.concat(" Lv. ").concat(entity.baseLevel))
        text.translateX = Config.tileSize.toDouble() / 2 - text.layoutBounds.width / 2
        text.translateY = 75.0

        val vbox = Group(barHP, barSP, text)

        val subView = EntityView()
        subView.addNode(vbox)

        subView.translateXProperty().bind(entity.positionComponent.xProperty())
        subView.translateYProperty().bind(entity.positionComponent.yProperty())

        return SubViewComponent(subView)
    }

    fun createCharacter(dataComponents: List<Component>, x: Int, y: Int): CharacterEntity {
        val char = CharacterEntity(dataComponents)
        char.typeComponent.value = EntityType.CHARACTER
        char.positionComponent.value = Point2D(x * Config.tileSize.toDouble(), y * Config.tileSize.toDouble())

        char.addComponent(makeCharacterSubView(char))

        return char
    }
}