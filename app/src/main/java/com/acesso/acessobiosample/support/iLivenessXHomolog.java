package com.acessobio.liveness;

import java.io.Serializable;
import java.util.HashMap;

public interface iLivenessX extends Serializable {
    public void onResultLiveness(HashMap result);
    public void onError(String error);
}
