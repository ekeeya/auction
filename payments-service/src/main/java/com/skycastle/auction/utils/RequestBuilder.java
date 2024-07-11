/*
 * Online auctioning system
 *
 * Copyright (c) 2023.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.utils;

import com.skycastle.auction.dtos.mtn.responses.EmptyResponseDTO;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Slf4j
public class RequestBuilder {

    private HttpHeaders headers;
    private HttpMethod method;
    private String url;
    private Object payload;
    private String pathVariable;
    private ResponseEntity<Object> response;
    private RestTemplate client;

    public static HttpHeaders generateBasicAuthHeader(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }
    public <T>  T sendRequest(Class<T> responseClass) throws InstantiationException, IllegalAccessException {
        if (responseClass == null){
            responseClass = (Class<T>) EmptyResponseDTO.class;
        }
        String URL =  this.pathVariable==null ? this.url : this.url.replace("{variable}", this.pathVariable);
        HttpEntity<Object> requestEntity = this.payload == null ? new HttpEntity<>(null, headers) : new HttpEntity<>(this.payload, headers);
        ResponseEntity<Object> responseEntity = client.exchange(URL,this.method,requestEntity,Object.class);

        Map<String, Object> mappedResponse = new HashMap<>();
        T returnObj =  responseClass.newInstance();
        List<Integer> okStatuses = List.of(new Integer[]{HttpStatus.OK.value(), HttpStatus.CREATED.value(), HttpStatus.ACCEPTED.value()});
        if ( okStatuses.contains(responseEntity.getStatusCode().value())){
            if (responseEntity.getBody() instanceof Map<?, ?> map) {
                if (map.keySet().stream().allMatch(key -> key instanceof String)) {
                    mappedResponse = (Map<String, Object>) map;
                    log.info(mappedResponse.toString());
                    Utils.setProperties(returnObj, mappedResponse);
                    return  returnObj;
                }
            }
            // we shall always pass empty responses
            mappedResponse.put("response", responseEntity.getBody());
            Utils.setProperties(returnObj, mappedResponse);
            return  returnObj;
        }else{
            String message =  String.format("Response Failed with status code %s : %s", responseEntity.getStatusCode().value(), responseEntity.toString());
            log.warn(message);
            return null;
        }

    }

}
