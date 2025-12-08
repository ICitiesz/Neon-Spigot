package com.islandstudio.neon.shared.core.config.obj.neonfeature

import com.islandstudio.neon.shared.core.config.obj.neonfeature.NBundleConfigObject.NBundleConfigOptions

interface IServerFeatureBaseConfigObject {
    var isEnabled: Boolean
    var options: NBundleConfigOptions
}