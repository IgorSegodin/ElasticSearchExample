package org.isegodin.example.elastic.search.dto;

import java.net.URLEncoder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author i.segodin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto implements Identifier<String> {
    String path;
    String name;
    Long size;
    String contentType;

    @Override
    @SneakyThrows
    public String getId() {
        return URLEncoder.encode(getPath(), "UTF-8");
    }
}
