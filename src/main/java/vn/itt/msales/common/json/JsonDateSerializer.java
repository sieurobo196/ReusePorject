/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

/**
* Used to serialize Java.util.Date, which is not a common JSON type, so we have
 * to create a custom serialize method;.
 * <p>
 * @author vtm036
 */
@Component("jacksonObjectMapper")
public class JsonDateSerializer extends JsonSerializer<Date> {

    private SimpleDateFormat dateFormat;

    public JsonDateSerializer() {
    }
    public JsonDateSerializer(String stringFormat) {
        dateFormat = new SimpleDateFormat("dd/mm/yyyy");
    }
    
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        String formattedDate = dateFormat.format(date);
        gen.writeString(formattedDate);
    }

}
