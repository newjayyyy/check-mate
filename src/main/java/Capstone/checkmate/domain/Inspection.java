package Capstone.checkmate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inspections")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Inspection {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @Column(name = "total_count")
    private int totalCount;
    @Column(name = "abnormal_count")
    private int abnormalCount;
    @Column(name = "abnormal_rate")
    private double abnormalRate;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL)
    private List<InspectionImage> inspectionImages = new ArrayList<>();


    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getInspections().add(this);
    }
    void setModel(Model model) {
        this.model = model;
        model.getInspections().add(this);
    }
    public void addImage(InspectionImage image) {
        this.inspectionImages.add(image);
        image.setInspection(this);
    }


    //==생성 메서드==//
    public static Inspection createInspection(Member member, Model model, InspectionImage... inspectionImages) {
        Inspection inspection = new Inspection();
        inspection.setMember(member);
        inspection.setModel(model);

        for(InspectionImage image : inspectionImages) {
            inspection.addImage(image);
        }

        inspection.uploadedAt = LocalDateTime.now();
        inspection.calculateAnomalyStats();

        return inspection;
    }

    //==비즈니스 로직==//

    /**
     * 총합, anomaly 개수/비율 계산
     */
    public void calculateAnomalyStats() {
        int cnt = (int) inspectionImages.stream()
                .filter(img -> img.getResult() == InspectResult.ABNORMAL)
                .count();

        this.totalCount = inspectionImages.size();
        this.abnormalCount = cnt;
        this.abnormalRate = totalCount == 0 ? 0.0 : (double) cnt / totalCount;
    }

}