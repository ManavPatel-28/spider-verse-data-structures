package spiderman;

import java.util.HashMap;

public class MachineData {
    private HashMap<Integer, Integer> distances;
    private HashMap<Integer, Integer> lineages;

    public HashMap<Integer, Integer> getDistances() {
        return distances;
    }

    public void setDistances(HashMap<Integer, Integer> newDistances){
        distances = newDistances;
    }

    public HashMap<Integer, Integer> getLineages() {
        return lineages;
    }


    public void setLineages(HashMap<Integer, Integer> newLineages){
        lineages = newLineages;
    }
}
