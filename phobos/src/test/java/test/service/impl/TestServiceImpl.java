package test.service.impl;

import test.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by lo on 1/16/17.
 */
public class TestServiceImpl implements TestService {
    private static Random r = new Random();
    private static Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);
    private static Map<String, Person> persons = new HashMap<>();
    static {
        r.setSeed(System.currentTimeMillis());
        Person person = new Person();
        person.setName("service user");
        person.setAge(18);
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address(AddressType.Home, "it's home address"));
        addresses.add(new Address(AddressType.Other, "it's other address"));
        person.setAddresses(addresses);
        persons.put(person.getName(), person);
    }

    @Override
    public void empty() {
        logger.info("empty call");
    }

    @Override
    public String hello(String name) {
        return "hello " + name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<Person> getPerson(String name) {
        Person person = persons.get(name);
        if (person == null) {
            return Result.error("no such person");
        }
        return new Result<>(person);
    }

    @Override
    public Result<Person> random() {
        return new Result<>(persons.get(r.nextInt(persons.size())));
    }
}
