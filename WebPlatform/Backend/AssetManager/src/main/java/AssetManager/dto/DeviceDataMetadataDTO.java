package AssetManager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class DeviceDataMetadataDTO {
    @Setter
    @Getter
    private List<String> buckets;
    private Map<String, List<String>> fieldKeys;
    private Map<String, String> latestTimestamps;
}
