
package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

@PublicEvolving
public class ByteSizeToken implements Token {
    private final String originalValue;
    private final long bytes;

    public ByteSizeToken(String value) {
        this.originalValue = value;
        this.bytes = parseValue(value);
    }

    @Override
    public Object value() {
        return bytes;
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(originalValue);
    }

    public long getBytes() {
        return bytes;
    }

    private long parseValue(String value) {
        String numStr = value.replaceAll("[^0-9.]", "");
        String unitStr = value.replaceAll("[0-9.]", "").trim().toUpperCase();

        double num = Double.parseDouble(numStr);

        switch (unitStr) {
            case "B": return (long) num;
            case "KB": return (long) (num * 1000);
            case "MB": return (long) (num * 1000 * 1000);
            case "GB": return (long) (num * 1000 * 1000 * 1000);
            case "TB": return (long) (num * 1000 * 1000 * 1000 * 1000L);
            case "KIB": return (long) (num * 1024);
            case "MIB": return (long) (num * 1024 * 1024);
            case "GIB": return (long) (num * 1024 * 1024 * 1024);
            case "TIB": return (long) (num * 1024 * 1024 * 1024 * 1024L);
            default: throw new IllegalArgumentException("Invalid byte size unit: " + unitStr);
        }
    }
}
