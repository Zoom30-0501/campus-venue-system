package com.club.venue.service;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcessService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    private static final String PROCESS_KEY = "activity_approval";

    /**
     * 启动审批流程并自动提交
     */
    public ProcessInstance startProcess(String activityId, String activityName, String applicant) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("activityId", activityId);
        variables.put("activityName", activityName);
        variables.put("applicant", applicant);

        ProcessInstance pi = runtimeService.startProcessInstanceByKey(
                PROCESS_KEY, activityId, variables);

        // 自动完成提交任务，流转到审批环节
        Task submitTask = taskService.createTaskQuery()
                .processInstanceId(pi.getId())
                .taskAssignee("applicant")
                .singleResult();
        if (submitTask != null) {
            taskService.complete(submitTask.getId());
        }

        return pi;
    }

    /**
     * 获取待审批任务列表
     */
    public List<Map<String, Object>> getPendingTasks(String username) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup("club_leader")
                .list();

        return tasks.stream().map(task -> {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", task.getId());
            map.put("taskName", task.getName());
            map.put("processInstanceId", task.getProcessInstanceId());
            Map<String, Object> vars = taskService.getVariables(task.getId());
            map.put("activityId", vars.get("activityId"));
            map.put("activityName", vars.get("activityName"));
            map.put("applicant", vars.get("applicant"));
            map.put("createTime", task.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 审批通过
     */
    public void approveTask(String taskId, boolean approved, String comment) {
        approveTask(taskId, approved, comment, null);
    }

    /**
     * 审批通过（支持传递额外流程变量，用于BPMN网关条件判断）
     */
    public void approveTask(String taskId, boolean approved, String comment, Map<String, Object> extraVars) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approved);

        if (extraVars != null) {
            variables.putAll(extraVars);
        }

        if (comment != null) {
            taskService.addComment(taskId, null, comment);
        }

        taskService.complete(taskId, variables);
    }

    /**
     * 通过业务ID获取当前审批任务
     */
    public Task getTaskByBusinessKey(String businessKey) {
        return taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
    }

    /**
     * 获取流程实例的审批状态
     */
    public boolean isProcessActive(String businessKey) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        return pi != null;
    }
}
