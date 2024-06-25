package DataManager.repository;

import com.google.gson.Gson;
import com.influxdb.client.*;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.BucketRetentionRules;
import com.influxdb.client.domain.Organization;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Repository
public class InfluxRepository {

    private final InfluxDBClient influxDBClient;
    private static final Logger log = LoggerFactory.getLogger(InfluxRepository.class);

    private final String org;


    @Autowired
    public InfluxRepository(@Value("${influxdb.url}") String url,
                            @Value("${influxdb.token}") String token,
                            @Value("${influxdb.org}") String org) {
        this.influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org);
        this.org = org;
    }


    public void saveData(Map<String, String> dataMap) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        BucketsApi bucketsApi = influxDBClient.getBucketsApi();
        dataMap.forEach((bucketName, data) -> {
            try {
                Bucket bucket = bucketsApi.findBucketByName(bucketName);
                if (bucket == null) {
                    log.info("Bucket '{}' not found. Creating new bucket.", bucketName);
                    BucketRetentionRules retention = new BucketRetentionRules();
                    retention.setEverySeconds(0); // Set to 0 for infinite retention
                    String orgId = getOrganizationId();
                    assert orgId != null;
                    bucketsApi.createBucket(bucketName, retention, orgId);
                }
                writeApi.writeRecord(bucketName, org, WritePrecision.S, data);
            }catch (InfluxException ie) {
                log.error("InfluxException: ", ie); } });
    }

    private String getOrganizationId() {
        OrganizationsApi organizationsApi = influxDBClient.getOrganizationsApi();
        List<Organization> organization = organizationsApi.findOrganizations();
        for (Organization org : organization) {
            if (org.getName().equals(this.org)) {
                return org.getId();
            }
        }
        return null;
    }

    public List<String> getBuckets(String deviceId) {
        String flux = "buckets()";
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux);
        List<String> allBuckets = tables.stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> record.getValueByKey("name").toString())
                .collect(Collectors.toList());

        return allBuckets.stream()
                .filter(bucket -> {
                    String checkDataFlux = String.format("from(bucket: \"%s\") |> range(start: -1y) |> filter(fn: (r) => r[\"learning_device_id\"] == \"%s\") |> limit(n: 1)", bucket, deviceId);
                    List<FluxTable> dataTables = queryApi.query(checkDataFlux);
                    return !dataTables.isEmpty() && !dataTables.get(0).getRecords().isEmpty();
                })
                .collect(Collectors.toList());
    }

    public List<String> getFieldKeys(String bucket, String deviceId, String measurement) {
        String flux = String.format("from(bucket: \"%s\") |> range(start: -1y) |> filter(fn: (r) => r[\"learning_device_id\"] == \"%s\" and r._measurement == \"%s\") |> keep(columns: [\"_field\"]) |> distinct(column: \"_field\")", bucket, deviceId, measurement);
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux);
        return tables.stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> record.getValueByKey("_field").toString())
                .collect(Collectors.toList());
    }

    public Instant getLatestTimestamp(String bucket, String deviceId, String measurement) {
        String flux = String.format("from(bucket: \"%s\") |> range(start: -1y) |> filter(fn: (r) => r[\"learning_device_id\"] == \"%s\" and r._measurement == \"%s\") |> sort(columns: [\"_time\"], desc: true) |> limit(n: 1)", bucket, deviceId, measurement);
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux);
        List<FluxRecord> records = tables.stream()
                .flatMap(table -> table.getRecords().stream())
                .collect(Collectors.toList());
        if (!records.isEmpty()) {
            return records.get(0).getTime();
        }
        return Instant.now();
    }

    public String getMeasurementsForDevice(String deviceId) {
        List<String> measurements = new ArrayList<>();
        List<String> buckets = getBuckets(deviceId);

        for (String bucketName : buckets) {
            String query = "from(bucket: \"" + bucketName + "\") |> range(start: -1y) |> filter(fn: (r) => r[\"learning_device_id\"] == \"" + deviceId + "\") |> keep(columns: [\"_measurement\"]) |> unique(column: \"_measurement\")";
            QueryApi queryApi = influxDBClient.getQueryApi();

            List<FluxTable> tables = queryApi.query(query);
            for (FluxTable table : tables) {
                for (FluxRecord record : table.getRecords()) {
                    String measurement = (String) record.getValueByKey("_measurement");
                    if (!measurements.contains(measurement)) {
                        measurements.add(measurement);
                    }
                }
            }
        }

        Gson gson = new Gson();
        String measurementsJson = gson.toJson(measurements);

        log.info("Measurements for device {}: {}", deviceId, measurementsJson);

        return measurementsJson;
    }

    public String getMetadataForDevice(String deviceId, String measurement) {
        List<String> buckets = getBucketsWithMeasurement(deviceId, measurement);
        Map<String, List<String>> fieldKeysMap = new HashMap<>();
        Map<String, String> latestTimestamps = new HashMap<>();

        for (String bucket : buckets) {
            List<String> fieldKeys = getFieldKeys(bucket, deviceId, measurement);
            if (!fieldKeys.isEmpty()) {
                fieldKeysMap.put(bucket, fieldKeys);
                Instant latestTimestamp = getLatestTimestamp(bucket, deviceId, measurement);
                latestTimestamps.put(bucket, latestTimestamp.toString());
            }
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("buckets", buckets);
        metadata.put("fieldKeys", fieldKeysMap);
        metadata.put("latestTimestamps", latestTimestamps);

        Gson gson = new Gson();
        String metadataJson = gson.toJson(metadata);

        // Log aggiunto prima di restituire il JSON
        log.info("Metadata for device {} and measurement {}: {}", deviceId, measurement, metadataJson);

        // Ritorno del JSON come stringa
        return metadataJson;
    }

    private List<String> getBucketsWithMeasurement(String deviceId, String measurement) {
        List<String> allBuckets = getBuckets(deviceId);
        return allBuckets.stream()
                .filter(bucket -> {
                    String checkDataFlux = String.format(
                            "from(bucket: \"%s\") |> range(start: -1y) |> filter(fn: (r) => r[\"learning_device_id\"] == \"%s\" and r._measurement == \"%s\") |> limit(n: 1)",
                            bucket, deviceId, measurement);
                    List<FluxTable> dataTables = influxDBClient.getQueryApi().query(checkDataFlux);
                    return !dataTables.isEmpty() && !dataTables.get(0).getRecords().isEmpty();
                })
                .collect(Collectors.toList());
    }

    public InputStreamResource getDeviceDataAsZip(String deviceId) throws IOException {
        List<String> buckets = getBuckets(deviceId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        for (String bucket : buckets) {
            String flux = String.format("from(bucket: \"%s\") |> range(start: -1y) |> filter(fn: (r) => r[\"learning_device_id\"] == \"%s\")", bucket, deviceId);
            List<FluxTable> tables = influxDBClient.getQueryApi().query(flux);
            if (!tables.isEmpty() && !tables.get(0).getRecords().isEmpty()) {
                String lineProtocolData = convertToLineProtocol(tables);
                ZipEntry zipEntry = new ZipEntry(bucket + ".line");
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(lineProtocolData.getBytes());
                zipOutputStream.closeEntry();
            }
        }

        zipOutputStream.close();
        return new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }

    private String convertToLineProtocol(List<FluxTable> tables) {
        StringBuilder lineProtocolBuilder = new StringBuilder();
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                String measurement = record.getMeasurement();
                String timestamp = String.valueOf(record.getTime().toEpochMilli());

                // Costruire i tag escludendo i campi e il timestamp
                String tags = record.getValues().entrySet().stream()
                        .filter(entry -> !entry.getKey().startsWith("_") && !entry.getKey().equals("result") && !entry.getKey().equals("table") && !entry.getKey().equals("_start") && !entry.getKey().equals("_stop") && !entry.getKey().equals("_time") && !entry.getKey().equals("_value"))
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining(","));

                // Costruire i campi includendo solo quelli che iniziano con "_value"
                String fields = record.getValues().entrySet().stream()
                        //.filter(entry -> entry.getKey().startsWith("_value") || entry.getKey().equals("_field"))
                        .filter(entry -> entry.getKey().equals("_field"))
                        .map(entry -> entry.getValue().toString() + "=" + record.getValue())
                        .collect(Collectors.joining(","));

                lineProtocolBuilder.append(measurement)
                        .append(",")
                        .append(tags)
                        .append(" ")
                        .append(fields)
                        .append(" ")
                        .append(timestamp)
                        .append("\n");
            }
        }
        return lineProtocolBuilder.toString();
    }
}
