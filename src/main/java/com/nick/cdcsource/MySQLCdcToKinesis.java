package com.nick.cdcsource;

import com.nick.model.function.AsyncCustomerOrderRequest;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class MySQLCdcToKinesis {
    private static final Logger log = LoggerFactory.getLogger(MySQLCdcToKinesis.class);

    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> orderSource = env.fromSource(
                createMySqlCDCSource(), WatermarkStrategy.noWatermarks(), "Person Source");

        DataStream<String> resultStream =
                AsyncDataStream.unorderedWait(orderSource, new AsyncCustomerOrderRequest(log), 1000, TimeUnit.MILLISECONDS, 100);


        resultStream.print();

        env.execute("Flink Streaming Java API From MySQL CDC to Screen.");
    }

    private static MySqlSource<String> createMySqlCDCSource() {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("my-hostname")
                .port(3306)
                .databaseList("test_db") // set captured database
                .tableList("test_db.Pereson") // set captured table
                .username("admin")
                .password("password")
                .startupOptions(StartupOptions.initial())
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        return mySqlSource;
    }
}
