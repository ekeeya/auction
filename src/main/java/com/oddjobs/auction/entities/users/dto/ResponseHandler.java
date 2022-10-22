package com.oddjobs.auction.entities.users.dto;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHandler {
    private HttpStatus status;
    private boolean error=false;
    private String message;
    private Object data;
    private long count;
    private long totalPages;
    private long size;



    private ResponseHandler(ResponseHandlerBuilder builder) {
        this.status = builder.status;
        this.error = builder.error;
        this.message = builder.message;
        this.data =  builder.data;
        this.count = builder.count;
        this.totalPages = builder.totalPages;
        this.size = builder.size;

    }

    public long getSize() {
        return size;
    }
    public long getCount() {
        return count;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ResponseHandler{" +
                "status=" + status +
                ", error=" + error +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static class ResponseHandlerBuilder{
        private HttpStatus status;
        private boolean error=false;
        private String message;
        private Object data;
        private long count;
        private long totalPages;
        private long size;

        public ResponseHandlerBuilder status(HttpStatus status){
            this.status = status;
            return this;
        }
        public ResponseHandlerBuilder error(boolean error){
            this.error = error;
            return this;
        }
        public ResponseHandlerBuilder message(String message){
            this.message = message;
            return this;
        }
        public ResponseHandlerBuilder data(Object data){
            this.data = data;
            return this;
        }
        public ResponseHandlerBuilder count(long count){
            this.count = count;
            return this;
        }
        public ResponseHandlerBuilder size(long size){
            this.size = size;
            return this;
        }
        public ResponseHandlerBuilder totalPages(long totalPages){
            this.totalPages = totalPages;
            return this;
        }

        public ResponseHandler build(){

            return new ResponseHandler(this);
        }

    }

    public static ResponseEntity<Object> generateResponse(ResponseHandler responseHandler){
        Map<String, Object> mapper = new HashMap<String, Object>();
        try{
            Object data =  responseHandler.getData();
            mapper.put("timestamp", new Date());
            mapper.put("status", responseHandler.getStatus().value());
            mapper.put("success", !responseHandler.isError());
            mapper.put("message", responseHandler.getMessage());
            mapper.put("data", data);
            if (data instanceof List && !responseHandler.isError()){
                mapper.put("count", responseHandler.getCount());
                mapper.put("size", responseHandler.getSize());
                mapper.put("totalPages", responseHandler.getTotalPages());
            }
            return new ResponseEntity<Object>(mapper, responseHandler.getStatus());

        }catch (Exception e){
            mapper.clear();
            mapper.put("timestamp", new Date());
            mapper.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            mapper.put("success",false);
            mapper.put("message", e.getMessage());
            mapper.put("data", null);
            return new ResponseEntity<Object>(mapper,responseHandler.getStatus());
        }
    }
}
