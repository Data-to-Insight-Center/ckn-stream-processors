package edu.d2i.ckn.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OracleEvent {
    private int image_count;

    @JsonProperty("UUID")
    private String uuid;

    private String image_name;
    private String ground_truth;
    private String image_receiving_timestamp;
    private String image_scoring_timestamp;
    private String model_id;
    private String label;
    private double probability;
    private String image_store_delete_time;
    private String image_decision;
    private String device_id;
}
