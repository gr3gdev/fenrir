package io.github.gr3gdev.benchmark.spring.controller;

import io.github.gr3gdev.benchmark.spring.bean.City;
import io.github.gr3gdev.benchmark.spring.dao.CityRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
public class CityController extends AbstractController<City> {

    public CityController(CityRepository cityRepository) {
        super(cityRepository);
    }
}
