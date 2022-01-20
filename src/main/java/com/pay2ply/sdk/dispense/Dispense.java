package com.pay2ply.sdk.dispense;

public class Dispense {
  private int id;
  private String username;
  private String server_token;
  private String command;
  private String external_reference;
  private int is_actived;

  public int getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getServerToken() {
    return server_token;
  }

  public String getCommand() {
    return command;
  }

  public int getIsActived() {
    return is_actived;
  }

}
