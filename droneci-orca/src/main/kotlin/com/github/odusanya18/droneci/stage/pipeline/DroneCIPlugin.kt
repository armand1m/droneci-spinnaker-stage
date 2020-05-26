package com.github.odusanya18.droneci.stage.pipeline

import com.github.odusanya18.droneci.stage.models.definition.CIStageDefinition
import com.github.odusanya18.droneci.stage.controller.DroneCIController
import com.github.odusanya18.droneci.stage.tasks.MonitorDroneCITask
import com.github.odusanya18.droneci.stage.tasks.StartDroneCITask
import com.netflix.spinnaker.orca.api.pipeline.graph.TaskNode.Builder
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution
import com.netflix.spinnaker.orca.api.pipeline.graph.StageDefinitionBuilder
import com.netflix.spinnaker.kork.plugins.api.spring.PrivilegedSpringPlugin
import com.netflix.spinnaker.orca.api.pipeline.CancellableStage
import org.pf4j.Extension
import org.pf4j.PluginWrapper
import org.springframework.beans.factory.support.BeanDefinitionRegistry

class DroneCIPlugin(wrapper: PluginWrapper) : PrivilegedSpringPlugin(wrapper) {
    override fun registerBeanDefinitions(registry: BeanDefinitionRegistry) {
        registerBean(beanDefinitionFor(DroneCIController::class.java), registry)
    }

    @Extension
    class DroneCIStage : StageDefinitionBuilder {
        override fun taskGraph(stage: StageExecution, builder: Builder) {
            builder
                .withTask("startDroneCITask", StartDroneCITask::class.java)
                .withTask("monitorDroneCITask", MonitorDroneCITask::class.java)
                .withTask("stopDroneCITask", MonitorDroneCITask::class.java)
        }
    }
}