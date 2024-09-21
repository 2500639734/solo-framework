package com.solo.framework.web.validation;

import lombok.Data;
import lombok.experimental.Delegate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationList<T> implements List<T> {

    @Delegate
    @Valid
    private final List<T> delegate = new ArrayList<>();

}
