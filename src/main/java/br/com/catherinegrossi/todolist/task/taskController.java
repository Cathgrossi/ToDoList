package br.com.catherinegrossi.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import br.com.catherinegrossi.todolist.utils.utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")

public class taskController {

    @Autowired
    private ItaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create (@RequestBody taskModel taskModel, HttpServletRequest request ){
        System.out.println("chegou no controller" );
        //autenticar e validar idUser
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);
       
        //validar data passada com a atual
        var currentDate = LocalDateTime.now();
        //se for maior
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt()) ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de inicio/termino dever ser maior do que a data atual");
        }

        //se for menor
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt()) ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de inicio dever ser menor do que a data de termino");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);

    }
    
    @GetMapping("/")
    public List<taskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

    //update das tarefas
    //http://localhost:8080/tasks/87654345-kgtrfgv-8765456
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody taskModel taskModel,@PathVariable UUID id, HttpServletRequest request){
    
        //lincar a tarefa com o user
        var task = this.taskRepository.findById(id).orElse(null);
       
        //ter o id correti
        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("tarefa não encontrada");
        }
        
        var idUser = request.getAttribute("idUser");

        //bloquear a liberação da task se o id for diferente
        if (!task.getIdUser().equals(idUser)) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("usuario nao tem permissao para alterar esse tarefa");
        }

        utils.copyNonNullProperties(taskModel, task);
        
        var taskUptaded = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUptaded);
    }
}
