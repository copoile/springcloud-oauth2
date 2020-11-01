package cn.poile.ucs.auth.common;

import cn.poile.ucs.auth.exception.CustomOauthTokenException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * @author: yaohw
 * @create: 2020/10/31 11:32 上午
 */
public class CustomOauthTokenExceptionJsonSerializer extends StdSerializer<CustomOauthTokenException> {


    protected CustomOauthTokenExceptionJsonSerializer() {
        super(CustomOauthTokenException.class);
    }

    @Override
    public void serialize(CustomOauthTokenException e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("code", e.getCode());
        String message = e.getMessage();
        if (StringUtils.isNotBlank(message)) {
            jsonGenerator.writeStringField("message", message);
        }
        jsonGenerator.writeEndObject();
    }
}
