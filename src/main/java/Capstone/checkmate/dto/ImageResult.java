package Capstone.checkmate.dto;

import Capstone.checkmate.domain.InspectResult;
import lombok.Data;

import java.net.URL;

@Data
public class ImageResult {
    private String fileName;
    private URL imageUrl;
    private InspectResult inspectResult;
}
