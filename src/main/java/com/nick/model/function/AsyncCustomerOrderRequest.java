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
    public void asyncInvoke(String s, ResultFuture<String> resultFuture) throws Exception {

    }
}
