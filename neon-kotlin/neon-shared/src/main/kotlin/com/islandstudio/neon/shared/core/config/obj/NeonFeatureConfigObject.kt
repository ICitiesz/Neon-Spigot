package com.islandstudio.neon.shared.core.config.obj

import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.obj.neonfeature.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeonFeatureConfigObject(
    var nHarvest: NHarvestConfigObject = NHarvestConfigObject(),
    var nCutter: NCutterConfigObject = NCutterConfigObject(),
    var nSmelter: NSmelterConfigObject = NSmelterConfigObject(),
    var nPVP: NPVPConfigObject = NPVPConfigObject(),
    var nWaypoints: NWaypointsConfigObject = NWaypointsConfigObject(),

    @SerialName("DurabilityPlus")
    var durabilityPlus: DurabilityPlusConfigObject = DurabilityPlusConfigObject(),
    var nBundle: NBundleConfigObject = NBundleConfigObject(),
    var nFireworks: NFireworksConfigObject = NFireworksConfigObject(),
    var nPainting: NPaintingConfigObject = NPaintingConfigObject()
): IConfigObject