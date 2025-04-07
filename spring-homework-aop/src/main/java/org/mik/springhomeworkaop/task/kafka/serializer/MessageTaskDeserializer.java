package org.mik.springhomeworkaop.task.kafka.serializer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTaskDeserializer<T> extends JsonDeserializer<T> {

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception ex){
            log.warn("Произошла ошибка десериализаций сообщения. Сообщение: {}. Топик: {}. Заголовки: {}",new String(data, StandardCharsets.UTF_8),topic,headers);
            return null;
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception ex) {
            log.warn("Произошла ошибка десериализаций сообщения. Сообщение: {} . Топик: {}",new String(data, StandardCharsets.UTF_8),topic);
            return null;
        }
    }
}
