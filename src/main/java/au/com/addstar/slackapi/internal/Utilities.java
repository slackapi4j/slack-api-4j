package au.com.addstar.slackapi.internal;

import java.util.Collections;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

public class Utilities
{
    /**
     * So I dont have to force type Collections.emptyMap() for parameters
     */
    public static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();
    
    /**
     * Parses the element as a unix timestamp
     * @param element The element to convert
     * @return The time in milliseconds
     */
    public static long getAsTimestamp(final JsonElement element)
    {
        final String raw = element.getAsString();
        
        final long time;
        if (raw.contains("."))
        {
            final double precTime = Double.parseDouble(raw);
            time = (long)(precTime * 1000);
        }
        else {
            time = Long.parseLong(raw) * 1000;
        }
        
        return time;
    }
    
    public static String getAsString(final JsonElement element)
    {
        if (element == null || element instanceof JsonNull) {
            return null;
        }
        
        return element.getAsString();
    }

    public static boolean getAsBoolean(final JsonElement element, final boolean def)
    {
        if (element == null || element instanceof JsonNull) {
            return def;
        }
        
        return element.getAsBoolean();
    }

    public static int getAsInt(final JsonElement element)
    {
        if (element == null || element instanceof JsonNull) {
            return 0;
        }

        return element.getAsInt();
    }
}
