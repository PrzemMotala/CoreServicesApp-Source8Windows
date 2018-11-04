package com.przemekm.coreservicesapp.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface FileLoader {
    List<String> TAGS_LIST = new ArrayList<>(
            Arrays.asList("clientId", "requestId", "name", "quantity", "price"));

    boolean load(File file);
}
