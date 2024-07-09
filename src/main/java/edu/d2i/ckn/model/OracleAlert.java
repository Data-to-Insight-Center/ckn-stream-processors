package edu.d2i.ckn.model;

import lombok.Data;

@Data
public class OracleAlert {
    private String alert_name;
    private String priority;
    private String description;
    private String source_topic;
    private long timestamp;
    private OracleEvent event_data;
}