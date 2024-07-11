/*
 * Njiwa Consumer RSP Server (SM-DP+) - Commercial
 *  - GSMA SGP.22 v2.3 SM-DP+ implementation. 
 * Copyright (C) 2022, Teal Communications Inc. - www.tealcommunications.com
 * Created by Paul Bagyenda - paulb@tealcommunications.com
 *
 * This program is not free software.
 * Portions of this software are based on software originally developed by the Njiwa Open Source M2M RSP Server Project
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Teal Communications. The intellectual and technical
 * concepts contained herein are proprietary to Teal Communications
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or
 * copyright law. Dissemination of this information or
 * reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Teal Communications.
 * No warranty, explicit or implicit, provided.
 */
package com.skycastle.auction.entities.users.dto;

import java.io.Serializable;

public class BaseResponseDTO implements Serializable {

  private static final long serialVersionUID = -8001795241473090377L;

  private boolean success = true;
  private String message;
  private Integer errorCode;
  private Long id;

  public BaseResponseDTO() {
    this.setSuccess(true);
    this.setMessage("");
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Integer getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
