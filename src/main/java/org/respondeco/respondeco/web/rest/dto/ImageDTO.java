package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.respondeco.respondeco.domain.Image;

/**
 * Created by Klaus on 26.11.2014.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ImageDTO {
    public Long id;
    public String name;

    public ImageDTO(Image image) {
        this.id = image.getId();
        this.name = image.getName();
    }
}