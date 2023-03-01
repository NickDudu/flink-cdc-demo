package com.nick.cdcsource;

import com.nick.model.function.AsyncCustomerOrderRequest;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class MySQLCdcToKinesis {
    private static final Logger log = LoggerFactory.getLogger(MySQLCdcToKinesis.class);

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
        configuration.setInteger(RestOptions.PORT,8848);
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.enableCheckpointing(3000);

        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(10000);

        DataStreamSource<String> orderSource = env.fromSource(
                createMySqlCDCSource(), WatermarkStrategy.noWatermarks(), "Person Source");

        DataStream<String> resultStream =
                AsyncDataStream.unorderedWait(orderSource, new AsyncCustomerOrderRequest(log), 1000, TimeUnit.MILLISECONDS, 100);


        resultStream.print("asyncStream===============================>");

        env.execute("Flink Streaming Java API From MySQL CDC to Screen.");
    }

    private static MySqlSource<String> createMySqlCDCSource() {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(33061)
                .databaseList("test_db") // set captured database
                .tableList("test_db.Person") // set captured table
                .username("root")
                .password("p4ssw0rd")
                .startupOptions(StartupOptions.initial())
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        return mySqlSource;
    }
}
