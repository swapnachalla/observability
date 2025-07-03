package com.sample.observability;

import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.query.Param; // Added import for @Param

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends PagingAndSortingRepository<ApplicationUser, Long> {
    List<ApplicationUser> findByName(@Param("name") String name);
    ApplicationUser findById(@Param("id") long id);
    ApplicationUser findByEmail(@Param("email") String email);
    List<ApplicationUser> findByActiveFlag(@Param("activeFlag") boolean activeFlag);
}