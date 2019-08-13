package com.divya.greetings.repository;

import com.divya.greetings.models.Greet;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class GreetRepositoryTest extends DataStoreRepositoryTest {
    private GreetRepository greetRepository;

    @Before
    public void setUp() {
        greetRepository = new GreetRepository(dbi, 1000);
    }

    @Test
    public void shouldInsertNewGreetingToDB() {
        Greet greet = new Greet("Hello world");

        greetRepository.saveGreetings(greet);

        List<Greet> fetchedGreetings = greetRepository.find();
        assertEquals(1, fetchedGreetings.size());
        assertEquals("Hello world", fetchedGreetings.get(0).getMessage());

    }
}
