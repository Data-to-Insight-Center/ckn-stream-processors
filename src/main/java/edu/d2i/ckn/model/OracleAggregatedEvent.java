package edu.d2i.ckn.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OracleAggregatedEvent {
    private String device_id;
    private String model_id;
    private Instant window_start;
    private Instant window_end;
    private double average_probability;
    private long event_count;
}