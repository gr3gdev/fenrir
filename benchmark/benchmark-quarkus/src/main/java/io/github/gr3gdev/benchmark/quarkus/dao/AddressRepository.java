package io.github.gr3gdev.benchmark.quarkus.dao;

import io.github.gr3gdev.benchmark.quarkus.bean.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
