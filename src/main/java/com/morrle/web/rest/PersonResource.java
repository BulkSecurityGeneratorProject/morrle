package com.morrle.web.rest;

import com.morrle.domain.Person;
import com.morrle.repository.PersonReposity;
import com.morrle.web.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonResource {

    private final Logger log = LoggerFactory.getLogger(PersonResource .class);

    private PersonReposity personReposity;

    @Autowired
    public PersonResource(PersonReposity personReposity) {
        this.personReposity = personReposity;
    }

    /**
     * PUT  /persons -> Create a new person.
     * @param person
     * @return
     * @throws URISyntaxException
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody Person person) throws URISyntaxException {
        log.debug("REST request to save person : {}", person);
        if (person.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new person cannot already have an ID").build();
        }
        personReposity.save(person);
        return ResponseEntity.created(new URI("/api/persons/" + person.getId())).build();
    }

    /**
     * PUT  /persons -> Updates an existing person.
     * @param person
     * @return
     * @throws URISyntaxException
     */
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@RequestBody Person person) throws URISyntaxException {
        log.debug("REST request to update person : {}", person);
        if(person.getId() == null){
            return create(person);
        }
        personReposity.save(person);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /persons/:id -> get the "id" person.
     * @param id
     * @param response
     * @return
     */
    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> get(@PathVariable Integer id, HttpServletResponse response) {
        log.debug("REST request to get person : {}", id);
        Person person = personReposity.findOne(id);
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    /**
     * GET  /persons -> get all the persons.
     * @param offset
     * @param limit
     * @return
     * @throws URISyntaxException
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Person>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                               @RequestParam(value = "per_page", required = false) Integer limit)
            throws URISyntaxException {
        log.debug("page:{}  pre_page:{}",offset,limit);
        Page<Person> page = personReposity.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/persons", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
