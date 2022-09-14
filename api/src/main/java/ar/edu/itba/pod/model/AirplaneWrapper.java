package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.List;

public class AirplaneWrapper implements Serializable {
    private String modelName;
    private List<Section> sections;
    private boolean valid;

    public String getModelName() {
        return modelName;
    }

    public List<Section> getSections() {
        return sections;
    }

    public AirplaneWrapper(String modelName, List<Section> sections, boolean valid) {
        this.modelName = modelName;
        this.sections = sections;
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }
}
