package io.heig.tasks.api.endpoints;

import io.heig.tasks.api.TasksApi;
import io.heig.tasks.api.model.NewTask;
import io.heig.tasks.api.model.Task;
import io.heig.tasks.entities.ExecutionEntity;
import io.heig.tasks.entities.TaskEntity;
import io.heig.tasks.repositories.ExecutionRepository;
import io.heig.tasks.repositories.TaskRepository;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-26T19:36:34.802Z")

@Controller
public class TasksApiController implements TasksApi {

    @Autowired
    public TaskRepository taskRepository;

    @Autowired
    private ExecutionRepository executionRepository;

    @Override
    public ResponseEntity<Task> getTaskById(@PathVariable("task_id")String taskId) {

        if(taskId == null){
            return new ResponseEntity<Task>(HttpStatus.NOT_FOUND);
        }

        TaskEntity taskEntity = taskRepository.findOne(taskId);
        if(taskEntity == null){
            return new ResponseEntity<Task>(HttpStatus.NOT_FOUND);
        }
        else{
            ArrayList<ExecutionEntity> executions = new ArrayList<>();
            if(taskEntity.getExecutions() != null) {
                for(ExecutionEntity e : taskEntity.getExecutions()) {
                    ExecutionEntity ee = executionRepository.findOne(e.getId());
                    executions.add(ee);
                }
            }

            taskEntity.setExecutions(executions);
            return new ResponseEntity<Task>(taskEntity.getDTO(), HttpStatus.OK);
        }

        // TODO: populate execution and set self path (preferably auto-generated)
    }

    @Override
    public ResponseEntity<List<Task>> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        for(TaskEntity t : taskRepository.findAll()){
            ArrayList<ExecutionEntity> executions = new ArrayList<>();
            if(t.getExecutions() != null) {
                for(ExecutionEntity e : t.getExecutions()) {
                    ExecutionEntity ee = executionRepository.findOne(e.getId());
                    executions.add(ee);
                }
            }

            t.setExecutions(executions);
            tasks.add(t.getDTO());
        }

        return new ResponseEntity<List<Task>>(tasks, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Task> postTask(@ApiParam(value = "The task details" ,required=true ) @RequestBody @Valid NewTask body) {
        TaskEntity t = new TaskEntity();
        t.setName(body.getName());
        t.setDescription(body.getDescription());
        t.setTaskId(body.getTaskId());
        t.setCreationDate(System.currentTimeMillis());
        t = taskRepository.insert(t);
        return new ResponseEntity<Task>(t.getDTO(), HttpStatus.CREATED);
    }
}
