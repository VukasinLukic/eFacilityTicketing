/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rentaCar.rentaCarBackend.JPARepo;

import com.rentaCar.rentaCarBackend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * @author Saki
 */
public interface ClientRepository extends JpaRepository<Client, String> {

    /*
     * @Query(value =
     * "SELECT * FROM person INNER JOIN breed on town on person.town = town.id WHERE person.surname like '%:searchParam%'"
     * , nativeQuery = true)
     * List<Person> getPersonByCriteria(@Param("searchParam") String searchParam);
     */

    @Query(value = "SELECT * FROM person INNER JOIN town on person.town = town.id WHERE jmbg=: personJmbg", nativeQuery = true)
    List<Client> getPerson(@Param("personJmbg") String personJmbg);

    /*
     * @Query(value =
     * "SELECT * FROM person INNER JOIN town on person.town = town.id", nativeQuery
     * = true)
     * List<Person> getAllPeople();
     */

    List<Client> findBySurnameStartsWith(String name);

    Client findByUsername(String username);

    boolean existsByJmbg(String jmbg);

    void deleteByJmbg(String jmbg);
}
