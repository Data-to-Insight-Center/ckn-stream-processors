package edu.d2i.ckn.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OracleAggregator {
    private long count = 0;
    private String model_id = "";
    private String image_decision = "";
    private double total_probability = 0.0;
    private double average_probability = 0.0;
    private String device_id = "";

    public OracleAggregator process(OracleEvent event) {
        this.model_id = event.getModel_id();
        this.device_id = event.getDevice_id();
        this.image_decision = event.getImage_decision();
        this.count ++;
        this.total_probability += event.getProbability();
        if (this.count == 0){
            this.total_probability = 0;
        }
        else {
            this.average_probability = this.total_probability/this.count;
        }
        return this;
    }
}
