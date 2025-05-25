package com.aws.rds.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aws.rds.demo.model.Student;
import com.aws.rds.demo.service.StudentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students/list";
    }

    @GetMapping("/new")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        return "students/register";
    }

    @PostMapping("/save")
    public String registerStudent(@Valid @ModelAttribute("student") Student student, 
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "students/register";
        }
        
        if (studentService.existsByEmail(student.getEmail())) {
            model.addAttribute("emailError", "Email already exists");
            return "students/register";
        }
        
        studentService.saveStudent(student);
        return "redirect:/students?success";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "students/edit";
    }

    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id, @Valid @ModelAttribute("student") Student student, 
                               BindingResult result) {
        if (result.hasErrors()) {
            return "students/edit";
        }
        
        student.setId(id);
        studentService.saveStudent(student);
        return "redirect:/students?success";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return "redirect:/students?delete";
    }
}