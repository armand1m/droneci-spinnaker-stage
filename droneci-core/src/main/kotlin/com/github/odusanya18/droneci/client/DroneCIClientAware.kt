package com.github.odusanya18.droneci.client

import com.github.odusanya18.droneci.config.DroneCIProperties

open class DroneCIClientAware(val droneCIProperties: DroneCIProperties) {

    protected fun clientForMaster(masterName: String): DroneCIClient {
        val master = droneCIProperties.getMasterByName(masterName)

        return DroneCIClient(
            master.baseUrl,
            master.token
        )
    }
}