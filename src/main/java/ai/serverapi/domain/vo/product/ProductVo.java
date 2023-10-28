package ai.serverapi.domain.vo.product;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductVo {

    private Long id;
    private String mainTitle;
    private String mainExplanation;
    private String productMainExplanation;
    private String productSubExplanation;
    private int originPrice;
    private int price;
    private String purchaseInquiry;
    private String origin;
    private String producer;
    private String mainImage;
    private String image1;
    private String image2;
    private String image3;

    public void updateImages(final String url, final List<String> fileNames) {
        for (int i = 0; i < fileNames.size(); i++) {
            switch (i) {
                case 0:
                    this.mainImage = String.format("%s/%s", url,
                        Optional.ofNullable(fileNames.get(0)).orElse(""));
                case 1:
                    this.image1 = String.format("%s/%s", url,
                        Optional.ofNullable(fileNames.get(1)).orElse(""));
                case 2:
                    this.image2 = String.format("%s/%s", url,
                        Optional.ofNullable(fileNames.get(2)).orElse(""));
                case 3:
                    this.image3 = String.format("%s/%s", url,
                        Optional.ofNullable(fileNames.get(3)).orElse(""));
            }
        }
    }
}
