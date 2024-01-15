package io.github.gr3gdev.benchmark.spring.controller;

import io.github.gr3gdev.benchmark.spring.bean.Person;
import io.github.gr3gdev.benchmark.spring.dao.PersonRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class PersonController extends AbstractController<Person> {

    public PersonController(PersonRepository personRepository) {
        super(personRepository);
    }
}
