package Capstone.checkmate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inspection_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class InspectionImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_image_id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InspectResult result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    public void setInspection(Inspection inspection) {
        this.inspection = inspection;
    }

    //==생성 메서드==//
    public static InspectionImage createInspectionImage(String imageUrl, InspectResult result) {
        InspectionImage inspectionImage = new InspectionImage();
        inspectionImage.imageUrl = imageUrl;
        inspectionImage.result = result;
        return inspectionImage;
    }
}