package com.oddjobs.auction.entities.users.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseForm {
    @Getter
    @Setter
    private List<ObjectError> errors;

    public List<Map<String, Object>> getReadableErrors(){
        List<Map<String, Object>> readableErrors = new ArrayList<>();

        for (ObjectError error:this.getErrors() ) {
            Map<String, Object> e = new HashMap<String, Object>();
            if(error instanceof FieldError){
                e.put("field", ((FieldError) error).getField());
            }
            e.put("errorMessage", error.getDefaultMessage());
            readableErrors.add(e);
        }
        return  readableErrors;
    }
}
