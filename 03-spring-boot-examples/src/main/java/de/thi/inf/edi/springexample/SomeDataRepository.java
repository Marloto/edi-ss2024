package de.thi.inf.edi.springexample;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface SomeDataRepository extends CrudRepository<SomeData, UUID> {

}
