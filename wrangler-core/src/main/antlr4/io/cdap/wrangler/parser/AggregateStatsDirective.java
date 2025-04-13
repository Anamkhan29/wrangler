package io.cdap.wrangler.parser.directive;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.ArrayList;
import java.util.List;

@PublicEvolving
@Plugin(type = Directive.TYPE)
@Name("aggregate-stats")
@Description("Aggregates byte size and time duration columns into totals or averages")
public class AggregateStatsDirective implements Directive {
    private String sizeColumn;
    private String timeColumn;
    private String sizeOutputColumn;
    private String timeOutputColumn;
    private String sizeOutputUnit = "MB";
    private String timeOutputUnit = "s";
    private String aggregationType = "total";
    
    private long totalBytes = 0;
    private long totalNanos = 0;
    private int rowCount = 0;

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
        builder.define("sizeColumn", TokenType.COLUMN_NAME);
        builder.define("timeColumn", TokenType.COLUMN_NAME);
        builder.define("sizeOutputColumn", TokenType.COLUMN_NAME);
        builder.define("timeOutputColumn", TokenType.COLUMN_NAME);
        builder.define("sizeOutputUnit", TokenType.IDENTIFIER, UsageDefinition.Optional.OPTIONAL);
        builder.define("timeOutputUnit", TokenType.IDENTIFIER, UsageDefinition.Optional.OPTIONAL);
        builder.define("aggregationType", TokenType.IDENTIFIER, UsageDefinition.Optional.OPTIONAL);
        return builder.build();
    }

    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        this.sizeColumn = ((ColumnName) args.value("sizeColumn")).value();
        this.timeColumn = ((ColumnName) args.value("timeColumn")).value();
        this.sizeOutputColumn = ((ColumnName) args.value("sizeOutputColumn")).value();
        this.timeOutputColumn = ((ColumnName) args.value("timeOutputColumn")).value();
        
        if (args.contains("sizeOutputUnit")) {
            this.sizeOutputUnit = ((Identifier) args.value("sizeOutputUnit")).value();
        }
        if (args.contains("timeOutputUnit")) {
            this.timeOutputUnit = ((Identifier) args.value("timeOutputUnit")).value();
        }
        if (args.contains("aggregationType")) {
            this.aggregationType = ((Identifier) args.value("aggregationType")).value();
        }
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        for (Row row : rows) {
            Object sizeValue = row.getValue(sizeColumn);
            Object timeValue = row.getValue(timeColumn);
            
            if (sizeValue != null) {
                ByteSize byteSize = new ByteSize(sizeValue.toString());
                totalBytes += byteSize.getBytes();
            }
            
            if (timeValue != null) {
                TimeDuration timeDuration = new TimeDuration(timeValue.toString());
                totalNanos += timeDuration.getNanoseconds();
            }
            
            rowCount++;
        }
        return rows;
    }

    @Override
    public List<Row> finalize() throws DirectiveExecutionException {
        Row result = new Row();
        
        // Calculate size in requested unit
        double finalSize = convertBytes(totalBytes, sizeOutputUnit);
        if ("average".equalsIgnoreCase(aggregationType)) {
            finalSize = finalSize / rowCount;
        }
        result.add(sizeOutputColumn, finalSize);
        
        // Calculate time in requested unit
        double finalTime = convertNanos(totalNanos, timeOutputUnit);
        if ("average".equalsIgnoreCase(aggregationType)) {
            finalTime = finalTime / rowCount;
        }
        result.add(timeOutputColumn, finalTime);
        
        List<Row> results = new ArrayList<>();
        results.add(result);
        return results;
    }

    private double convertBytes(long bytes, String unit) {
        switch (unit.toUpperCase()) {
            case "B": return bytes;
            case "KB": return bytes / 1000.0;
            case "MB": return bytes / (1000.0 * 1000);
            case "GB": return bytes / (1000.0 * 1000 * 1000);
            case "TB": return bytes / (1000.0 * 1000 * 1000 * 1000);
            case "KIB": return bytes / 1024.0;
            case "MIB": return bytes / (1024.0 * 1024);
            case "GIB": return bytes / (1024.0 * 1024 * 1024);
            case "TIB": return bytes / (1024.0 * 1024 * 1024 * 1024);
            default: throw new IllegalArgumentException("Invalid output size unit: " + unit);
        }
    }

    private double convertNanos(long nanos, String unit) {
        switch (unit.toLowerCase()) {
            case "ns": return nanos;
            case "ms": return nanos / 1_000_000.0;
            case "s": return nanos / 1_000_000_000.0;
            case "m": return nanos / (60.0 * 1_000_000_000);
            case "h": return nanos / (60.0 * 60 * 1_000_000_000);
            case "d": return nanos / (24.0 * 60 * 60 * 1_000_000_000);
            default: throw new IllegalArgumentException("Invalid output time unit: " + unit);
        }
    }
}
