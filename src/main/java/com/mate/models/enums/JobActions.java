package com.mate.models.enums;

public enum JobActions {
  PAUSE {
    @Override
    public boolean status() {
      return true;
    }
  },
  RESUME {
    @Override
    public boolean status() {
      return false;
    }
  };

  public abstract boolean status();
}
