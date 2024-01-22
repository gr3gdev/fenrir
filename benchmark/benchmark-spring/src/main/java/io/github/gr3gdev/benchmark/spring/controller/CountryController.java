package io.github.gr3gdev.benchmark.spring.controller;

import io.github.gr3gdev.benchmark.domain.Country;
import io.github.gr3gdev.benchmark.spring.dao.CountryRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/country")
public class CountryController extends AbstractController<Country> {

    public CountryController(CountryRepository countryRepository) {
        super(countryRepository);
    }
}
