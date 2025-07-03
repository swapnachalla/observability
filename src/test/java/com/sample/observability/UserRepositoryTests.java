package com.sample.observability;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindByName() {
        ApplicationUser user = new ApplicationUser();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setActiveFlag(true);
        userRepository.save(user);

        List<ApplicationUser> found = userRepository.findByName("Alice");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void testFindByEmail() {
        ApplicationUser user = new ApplicationUser();
        user.setName("Bob");
        user.setEmail("bob@example.com");
        user.setActiveFlag(false);
        userRepository.save(user);

        ApplicationUser found = userRepository.findByEmail("bob@example.com");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Bob");
    }

    @Test
    void testFindByActiveFlag() {
        ApplicationUser user1 = new ApplicationUser();
        user1.setName("Carol");
        user1.setEmail("carol@example.com");
        user1.setActiveFlag(true);
        userRepository.save(user1);

        ApplicationUser user2 = new ApplicationUser();
        user2.setName("Dave");
        user2.setEmail("dave@example.com");
        user2.setActiveFlag(false);
        userRepository.save(user2);

        List<ApplicationUser> activeUsers = userRepository.findByActiveFlag(true);
        assertThat(activeUsers).extracting(ApplicationUser::getName).contains("Carol");
        List<ApplicationUser> inactiveUsers = userRepository.findByActiveFlag(false);
        assertThat(inactiveUsers).extracting(ApplicationUser::getName).contains("Dave");
    }
}
