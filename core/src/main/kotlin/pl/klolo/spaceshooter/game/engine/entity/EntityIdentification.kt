package pl.klolo.spaceshooter.game.engine.entity

fun isPlayerByName(entity: Entity): Boolean {
    return entity.uniqueName.contains("player")
}

fun isShieldByName(entity: Entity): Boolean {
    return entity.uniqueName.contains("shield")
}

fun isEnemyByName(entity: Entity): Boolean {
    return entity.uniqueName.contains("enemy")
            || entity.uniqueName.contains("boss")
}

fun isEnemyLaser(entity: Entity): Boolean {
    return entity.uniqueName.contains("laserRed")
}

fun isObstacle(entity: Entity): Boolean {
    return entity.uniqueName.contains("rock")
}

fun isPlayerLaser(entity: Entity): Boolean {
    return entity.uniqueName.contains("laserBlue")
}

fun isExtraPointsBonus(entity: Entity): Boolean {
    return entity.uniqueName == "silverStarBonus"
            || entity.uniqueName == "goldStarBonus"
}

fun isExtraBonus(entity: Entity): Boolean {
    return entity.uniqueName.contains("Bonus")
}

