package Capstone.checkmate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PredictResponse {

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("anomaly_score")
    private double anomalyScore;

    @JsonProperty("is_abnormal")
    private boolean isAbnormal;
}
