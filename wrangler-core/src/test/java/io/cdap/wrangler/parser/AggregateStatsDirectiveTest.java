package io.cdap.wrangler.parser.directive;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.executor.TestingRig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsDirectiveTest {
    @Test
    public void testAggregateStats() throws Exception {
        List<Row> rows = Arrays.asList(
            new Row("data_transfer_size", "1.5MB").add("response_time", "500ms"),
            new Row("data_transfer_size", "2KB").add("response_time", "1.2s"),
            new Row("data_transfer_size", "500B").add("response_time", "300ms")
        );
        
        String[] recipe = new String[] {
            "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
        };
        
        List<Row> results = TestingRig.execute(recipe, rows);
        
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(1.5075, (Double) results.get(0).getValue("total_size_mb"), 0.0001);
        Assert.assertEquals(2.0, (Double) results.get(0).getValue("total_time_sec"), 0.0001);
    }
    
    @Test
    public void testAverageAggregateStats() throws Exception {
        List<Row> rows = Arrays.asList(
            new Row("size", "1MB").add("time", "500ms"),
            new Row("size", "1MB").add("time", "500ms")
        );
        
        String[] recipe = new String[] {
            "aggregate-stats :size :time avg_size_mb avg_time_sec average"
        };
        
        List<Row> results = TestingRig.execute(recipe, rows);
        
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(1.0, (Double) results.get(0).getValue("avg_size_mb"), 0.0001);
        Assert.assertEquals(0.5, (Double) results.get(0).getValue("avg_time_sec"), 0.0001);
    }
}
