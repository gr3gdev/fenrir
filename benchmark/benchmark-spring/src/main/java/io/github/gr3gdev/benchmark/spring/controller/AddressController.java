package io.github.gr3gdev.benchmark.spring.controller;

import io.github.gr3gdev.benchmark.domain.Address;
import io.github.gr3gdev.benchmark.spring.dao.AddressRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
public class AddressController extends AbstractController<Address> {

    public AddressController(AddressRepository addressRepository) {
        super(addressRepository);
    }
}
