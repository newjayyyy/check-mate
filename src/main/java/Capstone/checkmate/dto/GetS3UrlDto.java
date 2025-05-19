package Capstone.checkmate.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetS3UrlDto {
    private String preSignedUrl;

    private String key;

    @Builder
    public GetS3UrlDto(String preSignedUrl, String key) {
        this.preSignedUrl = preSignedUrl;
        this.key = key;
    }
}