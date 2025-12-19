package com.cloudgarden.repository;

import com.cloudgarden.model.Succulent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucculentRepository extends JpaRepository<Succulent, Long> {

    List<Succulent> findByStatus(Succulent.Status status);

    List<Succulent> findByStatusNot(Succulent.Status status);
}
