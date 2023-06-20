package com.example.restservice.employee;

import com.example.restservice.errors.EmployeeExistsEx;
import com.example.restservice.errors.EmployeeNotFoundEx;
import com.example.restservice.rabbitmq.Producer;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping("/employees")
@RestController
public class EmployeeController {

    private final EmployeeRepository repository = new EmployeeRepositoryImpl();
    private final Producer producer;

    public EmployeeController() throws IOException, TimeoutException {
        producer  = new Producer();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) {
        System.out.println("dasfdasf");
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            Path filePath = Path.of("C:\\Users\\mateu\\IdeaProjects\\RestService\\src\\main\\resources\\users").resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok().body(filePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable int id){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return ResponseEntity.ok().headers(headers).body(producer.sendImage(repository.getEmployee(id).getPhoto()));
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    public EntityModel<Employee> getEmployee(@PathVariable int id){
        try{
            producer.send("called GET "+id,"getEmployee");
            Employee employee = repository.getEmployee(id);
            if (Objects.requireNonNull(employee.getStatus()) == EmployeeStatus.BUSY) {
                employee.setStatus(EmployeeStatus.AVAILABLE);
                return
                        EntityModel.of(repository.getEmployee(id),
                                linkTo(methodOn(EmployeeController.class).changeStatus(id)).withSelfRel(),
                                linkTo(methodOn(EmployeeController.class).getEmployees()).withRel("list all"));
            }
            employee.setStatus(EmployeeStatus.BUSY);
            return
                    EntityModel.of(repository.getEmployee(id),
                            linkTo(methodOn(EmployeeController.class).changeStatus(id)).withSelfRel(),
                            linkTo(methodOn(EmployeeController.class).deleteEmployee(id)).withRel("delete"),
                            linkTo(methodOn(EmployeeController.class).getEmployees()).withRel("list all"));
        }
        catch(EmployeeNotFoundEx ex){
            System.out.println("GET Exception");
            throw ex;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    public CollectionModel<EntityModel<Employee>> getEmployees() {
        try {
            producer.send("called GET ALL","getEmployees");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<EntityModel<Employee>> employees =
                repository.getAllEmployees().stream().map( employee -> {
                            if (employee.getStatus() == EmployeeStatus.AVAILABLE){
                                return EntityModel.of(employee,
                                        linkTo(methodOn(EmployeeController.class)
                                                .getEmployee(employee.getId())).withSelfRel(),
                                        linkTo(methodOn(EmployeeController.class)
                                                .deleteEmployee(employee.getId())).withRel("delete"),
                                        linkTo(methodOn(EmployeeController.class)
                                                .getEmployees()).withRel("list all")
                                );
                            }
                                    return EntityModel.of(employee,
                                            linkTo(methodOn(EmployeeController.class)
                                                    .getEmployee(employee.getId())).withSelfRel(),
                                            linkTo(methodOn(EmployeeController.class)
                                                    .getEmployees()).withRel("list all")
                                    );
                        }
                ).collect(Collectors.toList());
        return CollectionModel.of(employees,
                linkTo(methodOn(EmployeeController.class)
                        .getEmployees()).withSelfRel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteEmployee(@PathVariable int id){

        try {
            producer.send("called DELETE "+id,"deleteEmployee");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            System.out.println("called DELETE");
            return ResponseEntity.ok(repository.deleteEmployee(id));
        }
        catch(EmployeeNotFoundEx ex){
            System.out.println("DELETE Exception");
            throw ex;
        }
    }

    @PostMapping()
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee){
        try {
            producer.send("called POST","postEmployee");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            System.out.println("called POST");
            return ResponseEntity.status(201).body(repository.addEmployee(repository.lastId(), employee.getFirstName(),
                    employee.getAge(), employee.getWage(), employee.getPhoto()));
        } catch (EmployeeExistsEx ex) {
            System.out.println("POST Exception");
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEmployee(@PathVariable int id, @RequestBody Employee employee){
        try {
            producer.send("called PUT "+id,"updateEmployee");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("called PUT "+id);
        repository.updateEmployee(id, employee.getFirstName(), employee.getAge(), employee.getWage());
        return ResponseEntity.status(204).build();
    }

    @PatchMapping("/change/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable int id){
        try {
            producer.send("called change "+id,"changeStatus");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Employee employee = repository.getEmployee(id);
        switch (employee.getStatus()){
            case BUSY -> {
                employee.setStatus(EmployeeStatus.AVAILABLE);
                return ResponseEntity.ok().body(
                        EntityModel.of(repository.getEmployee(id),
                                linkTo(methodOn(EmployeeController.class).changeStatus(id)).withSelfRel(),
                                linkTo(methodOn(EmployeeController.class).getEmployees()).withRel("list all")));
            }
            default -> {
                employee.setStatus(EmployeeStatus.BUSY);
                return ResponseEntity.ok().body(
                        EntityModel.of(repository.getEmployee(id),
                                linkTo(methodOn(EmployeeController.class).changeStatus(id)).withSelfRel(),
                                linkTo(methodOn(EmployeeController.class).deleteEmployee(id)).withRel("delete"),
                                linkTo(methodOn(EmployeeController.class).getEmployees()).withRel("list all")));
            }

        }
    }
    }
