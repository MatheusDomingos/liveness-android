package com.acesso.acessobiosample.support;

import java.io.Serializable;
import java.util.HashMap;

public interface iLivenessXHomolog extends Serializable {
    public void onResultLiveness(HashMap result);
    public void onError(String error);
}
