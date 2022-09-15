package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.List;

public class AirplaneWrapper implements Serializable {
    private final String modelName;
    private final List<Section> sections;

    public String getModelName() {
        return modelName;
    }

    public List<Section> getSections() {
        return sections;
    }

    public AirplaneWrapper(String modelName, List<Section> sections) {
        this.modelName = modelName;
        this.sections = sections;
    }

}
