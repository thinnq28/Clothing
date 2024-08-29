package com.datn.shop_app.response.image;

import com.datn.shop_app.entity.Image;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NotEmpty
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponse {
    private Integer id;
    private String url;

    public static ImageResponse fromImage(Image image) {
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setId(image.getId());
        imageResponse.setUrl(imageResponse.getUrl());
        return imageResponse;
    }
}
