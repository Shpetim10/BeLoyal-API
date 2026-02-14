package com.shabanaj.beloyal.Repository;

import com.shabanaj.beloyal.Entity.Business;
import com.shabanaj.beloyal.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {

}
