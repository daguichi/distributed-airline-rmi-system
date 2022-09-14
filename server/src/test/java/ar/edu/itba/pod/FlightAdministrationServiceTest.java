package ar.edu.itba.pod;

import ar.edu.itba.pod.model.Airplane;
import ar.edu.itba.pod.model.Category;
import ar.edu.itba.pod.model.Section;
import ar.edu.itba.pod.server.Servant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlightAdministrationServiceTest {

    Servant servant = new Servant();

    private final String planeName = "TEST";
    private final Section business = new Section(Category.BUSINESS, 3, 5);
    private final Section economy = new Section(Category.ECONOMY, 5, 10);
    private final Section invalidSection = new Section(Category.ECONOMY, -1, 10);
    private final List<Section> sectionList = Arrays.asList(business, economy);
    private final List<Section> sectionList2 = Collections.singletonList(economy);

    @Test
    public void addValidAirplane() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        Assertions.assertTrue(servant.getAirplanes().containsKey(planeName));
    }

    @Test
    public void addRepeatedAirplane() throws RemoteException {
        servant.addPlaneModel(planeName, sectionList);
        servant.addPlaneModel(planeName, sectionList2);
        Assertions.assertTrue(servant.getAirplanes().containsKey(planeName));
        Assertions.assertEquals(Category.BUSINESS ,servant.getAirplanes().get(planeName).getSeats().get(0).get(0).getCategory());
    }

    @Test
    public void addInvalidSectionAirplane() throws RemoteException {
        servant.addPlaneModel(planeName, Collections.singletonList(invalidSection));
        Assertions.assertFalse(servant.getAirplanes().containsKey(planeName));
    }
}
