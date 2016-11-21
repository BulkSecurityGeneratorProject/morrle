package com.morrle.repository;


import com.morrle.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonReposity extends JpaRepository<Person,Integer> {
}
