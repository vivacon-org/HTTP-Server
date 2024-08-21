package org.vivacon.framework.serialization.json.serializer;

import org.vivacon.framework.serialization.common.StrGenerator;

public interface JsonSerializer<T> {

    StrGenerator serialize(Object obj, JsonGenerator gen, JsonSerializationContext context);
}
