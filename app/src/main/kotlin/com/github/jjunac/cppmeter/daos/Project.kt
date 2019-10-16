package com.github.jjunac.cppmeter.daos

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

class Project(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Project>(Projects)

    var name by Projects.name
    var path by Projects.path
}

object Projects : IntIdTable() {
    var name = varchar("name", 24).index()
    var path = varchar("path", 96)

}