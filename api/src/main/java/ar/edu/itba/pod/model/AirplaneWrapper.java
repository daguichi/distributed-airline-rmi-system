package ar.edu.itba.pod.model;

import java.io.Serializable;
import java.util.List;

public class AirplaneWrapper implements Serializable {
    private String modelName;
    private List<Section> sections;

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
