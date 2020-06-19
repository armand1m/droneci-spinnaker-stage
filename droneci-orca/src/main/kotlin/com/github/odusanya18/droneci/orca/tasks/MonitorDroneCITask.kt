package com.github.odusanya18.droneci.orca.tasks

import com.github.odusanya18.droneci.client.DroneCIClientAware
import com.github.odusanya18.droneci.config.DroneCIProperties
import com.github.odusanya18.droneci.orca.models.execution.BuildStatus
import com.github.odusanya18.droneci.orca.models.execution.DroneCIStageDefinition
import com.github.odusanya18.droneci.orca.util.TaskUtil.task
import com.github.odusanya18.droneci.orca.util.TaskUtil.taskResult
import com.google.gson.Gson
import com.netflix.spinnaker.orca.api.pipeline.RetryableTask
import com.netflix.spinnaker.orca.api.pipeline.TaskResult
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution
import org.pf4j.Extension
import java.util.concurrent.TimeUnit

@Extension
class MonitorDroneCITask(droneCIProperties: DroneCIProperties) : RetryableTask, DroneCIClientAware(
    droneCIProperties
) {

    override fun getTimeout() = TimeUnit.SECONDS.toMillis(droneCIProperties.timeout)
    override fun getBackoffPeriod() = TimeUnit.SECONDS.toMillis(droneCIProperties.backOffPeriod)

    override fun execute(stage: StageExecution): TaskResult {
        val execution = stage.mapTo(DroneCIStageDefinition::class.java)
        val infoBuild = clientForMaster(execution.master)
                .buildService
                .infoBuild(execution.namespace, execution.repo, execution.buildInfo!!.number)
                .execute()
        if (infoBuild.isSuccessful){
            infoBuild
                    .body()
                    ?.let {
                        return when (BuildStatus.valueOf(it.status)) {
                            BuildStatus.success -> taskResult(ExecutionStatus.SUCCEEDED, it)
                            BuildStatus.failed, BuildStatus.error -> taskResult(ExecutionStatus.TERMINAL, it)
                            BuildStatus.pending, BuildStatus.running -> taskResult(ExecutionStatus.RUNNING, it)
                        }
                    }
        }
        return taskResult(ExecutionStatus.TERMINAL, task("failed", execution.buildInfo.number))
    }

}
