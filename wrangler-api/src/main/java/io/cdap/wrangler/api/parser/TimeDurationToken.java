
package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

@PublicEvolving
public class TimeDurationToken implements Token {
    private final String originalValue;
    private final long nanoseconds;

    public TimeDurationToken(String value) {
        this.originalValue = value;
        this.nanoseconds = parseValue(value);
    }

    @Override
    public Object value() {
        return nanoseconds;
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(originalValue);
    }

    public long getNanoseconds() {
        return nanoseconds;
    }

    public long getMilliseconds() {
        return nanoseconds / 1_000_000;
    }

    public long getSeconds() {
        return nanoseconds / 1_000_000_000;
    }

    private long parseValue(String value) {
        String numStr = value.replaceAll("[^0-9.]", "");
        String unitStr = value.replaceAll("[0-9.]", "").trim().toLowerCase();

        double num = Double.parseDouble(numStr);

        switch (unitStr) {
            case "ns": return (long) num;
            case "ms": return (long) (num * 1_000_000);
            case "s": return (long) (num * 1_000_000_000);
            case "m": return (long) (num * 60 * 1_000_000_000L);
            case "h": return (long) (num * 60 * 60 * 1_000_000_000L);
            case "d": return (long) (num * 24 * 60 * 60 * 1_000_000_000L);
            default: throw new IllegalArgumentException("Invalid time unit: " + unitStr);
        }
    }
}
