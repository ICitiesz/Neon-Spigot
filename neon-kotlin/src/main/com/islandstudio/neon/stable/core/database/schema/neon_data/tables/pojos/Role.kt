/*
 * This file is generated by jOOQ.
 */
package com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos


import java.io.Serializable


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
data class Role(
    var roleId: Long? = null,
    var roleDisplayName: String? = null,
    var roleCode: String? = null,
    var assignedPlayerCount: Long? = null
): Serializable {


    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (this::class != other::class)
            return false
        val o: Role = other as Role
        if (this.roleId == null) {
            if (o.roleId != null)
                return false
        }
        else if (this.roleId != o.roleId)
            return false
        if (this.roleDisplayName == null) {
            if (o.roleDisplayName != null)
                return false
        }
        else if (this.roleDisplayName != o.roleDisplayName)
            return false
        if (this.roleCode == null) {
            if (o.roleCode != null)
                return false
        }
        else if (this.roleCode != o.roleCode)
            return false
        if (this.assignedPlayerCount == null) {
            if (o.assignedPlayerCount != null)
                return false
        }
        else if (this.assignedPlayerCount != o.assignedPlayerCount)
            return false
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (if (this.roleId == null) 0 else this.roleId.hashCode())
        result = prime * result + (if (this.roleDisplayName == null) 0 else this.roleDisplayName.hashCode())
        result = prime * result + (if (this.roleCode == null) 0 else this.roleCode.hashCode())
        result = prime * result + (if (this.assignedPlayerCount == null) 0 else this.assignedPlayerCount.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Role (")

        sb.append(roleId)
        sb.append(", ").append(roleDisplayName)
        sb.append(", ").append(roleCode)
        sb.append(", ").append(assignedPlayerCount)

        sb.append(")")
        return sb.toString()
    }
}
