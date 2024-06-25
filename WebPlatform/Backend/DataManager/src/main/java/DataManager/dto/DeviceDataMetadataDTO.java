package DataManager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Setter
@Getter
public class DeviceDataMetadataDTO {

    private List<String> buckets;
    private Map<String, List<String>> fieldKeys;
    private Map<String, String> latestTimestamps;
}
