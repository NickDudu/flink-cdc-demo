package com.nick.model.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.model.item.Person;
//import model.item.CustomerOrder;
//import model.item.Order;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;

public class AsyncCustomerOrderRequest extends RichAsyncFunction<String, String> {

    private Connection connection;
    private Logger log;

    public AsyncCustomerOrderRequest(Logger log) {
        this.log = log;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        connection = getConnection();
    }


    private Connection getConnection() {
        Connection con = null;
        try {
            log.info("Start to prepare JDBC connection.");
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:33061/test_db",
                    "root", "p4ssw0rd");
            log.info("Successfully prepare JDBC connection.");
        } catch (Exception e) {
            log.info("-----------mysql get connection has exception , msg = "+ e.getMessage());
        }
        return con;
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (connection != null) {// close the connection and release the resource
            connection.close();
        }
    }

    @Override
    public void asyncInvoke(String cdcString, ResultFuture<String> resultFuture) throws Exception {
        log.info("CDC String received: " + cdcString);
//      Below is a simple example of converting a JSON String to a Java object using the ObjectMapper class:
        ObjectMapper mapper = new ObjectMapper();
        if (cdcString.contains("\"op\":\"c\"")) {
            int start = cdcString.indexOf("\"after\":") + 8;
            int end = cdcString.indexOf("}")+ 1;
            String personString = cdcString.substring(start, end);
            log.info("start: " + start + ", end: " + end + ", personString: " + personString);

            Person person = mapper.readValue(personString, Person.class);

            log.info("Creating statement to query MySQL.");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select firstName, lastName, age, phone from Person where firstName = \"%s\"", person.firstName));
            log.info("Successfully executed the query, result is: " + resultSet);

            if (resultSet != null && resultSet.next()) {
                Person person1 = new Person(
                        resultSet.getString("firstName"),
                        resultSet.getString("firstName"),
                        resultSet.getInt("age"),
                        resultSet.getString("phone")
                );
                log.info("Person is: " + person);
                resultFuture.complete(Collections.singleton(mapper.writeValueAsString(person)));
            } else {
                log.info("No resultSet is returned, hence returning empty list.");
                resultFuture.complete(Collections.EMPTY_LIST);
            }
        }
        resultFuture.complete(Collections.EMPTY_LIST);

    }
}
