package com.example.todolist

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("tasks")
class TaskController(private val taskRepository: TaskRepository) {

    @GetMapping("")
    fun index(model: Model): String {
        val tasks = taskRepository.findAll()
        model.addAttribute("tasks", tasks)

        println("TaskController:index")
        return "tasks/index"
    }

    @GetMapping("new")
    fun new(form: TaskCreateForm): String {

        println("TaskController:new")

        return "tasks/new"
    }

    @PostMapping("")
    fun create(@Validated form: TaskCreateForm, bindingResult: BindingResult): String {
        if(bindingResult.hasErrors())
            return "tasks/new"

        val content = requireNotNull(form.content)
        taskRepository.create(content)

        println("TaskController:create")

        return "redirect:/tasks"
    }

    @GetMapping("{id}/edit")
    fun edit(@PathVariable("id") id: Long, form: TaskUpdateForm): String {
        val task = taskRepository.findById(id) ?: throw NotFoundException()
        form.content = task.content
        form.done = task.done
        println("TaskController:edit:content=${task.content}")
        return "tasks/edit"
    }

//    @PatchMapping("{id}")
    @PostMapping("{id}")
    fun update(@PathVariable("id") id: Long,
               @Validated form: TaskUpdateForm,
               bindingResult: BindingResult): String {
        println("TaskController:update")
        if(bindingResult.hasErrors())
            return "tasks/edit"

        val task = taskRepository.findById(id) ?: throw NotFoundException()
        val newTask = task.copy(content = requireNotNull(form.content), done = form.done)
        taskRepository.update(newTask)
        return "redirect:/tasks"
    }
}
