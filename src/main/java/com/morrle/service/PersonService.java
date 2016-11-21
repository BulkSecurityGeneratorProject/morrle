package com.morrle.service;

import com.morrle.domain.Person;
import com.morrle.repository.PersonReposity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonService {

    private final Logger log = LoggerFactory.getLogger(PersonService.class);

    private PersonReposity reposity;

    @Autowired
    public PersonService(PersonReposity reposity) {
        this.reposity = reposity;
    }

    @Transactional(readOnly = true)
    public List<Person> findAll(){
        try{
            log.debug("调用 PersonService findAll方法");
            return reposity.findAll();
        }catch (Exception e){
            log.error("调用 PersonService 出错" + e);
        }
        return null;
    }

    @Transactional
    public Person saveAndFlush(Person person){
        try{
            log.debug("调用 PersonService saveAndFlush 方法");
            return reposity.saveAndFlush(person);
        }catch (Exception e){
            log.error("调用 saveAndFlush 出错" + e);
        }
        return null;
    }

    @Transactional
    public void delete(Person person){
        try{
            log.debug("调用 PersonService delete 方法");
            reposity.delete(person);
        }catch (Exception e){
            log.error("调用 delete 出错" + e);
        }
    }


    @Transactional(readOnly = true)
    public Person findOne(Integer id) {
        return reposity.findOne(id);
    }
}
