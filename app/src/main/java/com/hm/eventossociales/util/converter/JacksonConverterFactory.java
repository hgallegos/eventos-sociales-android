package com.hm.eventossociales.util.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import dk.nykredit.jackson.dataformat.hal.HALMapper;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * Created by hans6 on 04-06-2017.
 */

public final class JacksonConverterFactory extends Converter.Factory {

    /** Create an instance using a default {@link HALMapper} instance for conversion. */
    public static JacksonConverterFactory create() {
        return create(new HALMapper());
    }

    /** Create an instance using {@code mapper} for conversion. */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static JacksonConverterFactory create(HALMapper mapper) {
        if (mapper == null) throw new NullPointerException("mapper == null");
        return new JacksonConverterFactory(mapper);
    }

    private final HALMapper mapper;

    private JacksonConverterFactory(HALMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectReader reader = mapper.readerFor(javaType);
        return new JacksonResponseBodyConverter<>(reader);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectWriter writer = mapper.writerFor(javaType);
        return new JacksonRequestBodyConverter<>(writer);
    }
}
