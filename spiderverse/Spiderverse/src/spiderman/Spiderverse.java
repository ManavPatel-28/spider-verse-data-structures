package spiderman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Spiderverse {
    private ArrayList<LinkedList<Dimension>> clusterTable;
    private LinkedList<Dimension> graph;
    private ArrayList<Integer> spotRoute;
    private HashMap<Integer, ArrayList<Integer>> anomalyRouteMap;
    private HashMap<String, Integer> anomalyTimeMap;
    private MachineData data;
    private boolean milesSaved;
    private int hubDimensionNumber;

    public void loadDimensions(String filename) {
        try {
            // Create cluster table
            StdIn.setFile(filename);

            int dimensions = StdIn.readInt();
            int initialSize = StdIn.readInt();
            double threshold = StdIn.readDouble();

            StdIn.readLine();

            int added = 0;
            int currentSize = initialSize;
            clusterTable = initializeTable(initialSize);

            for (int i = 0; i < dimensions; i++) {
                int number = StdIn.readInt();
                int events = StdIn.readInt();
                int weight = StdIn.readInt();
                int hashIndex = number % currentSize;

                clusterTable.get(hashIndex).addFirst(new Dimension(number, events, weight));
                added++;

                // Double and Rehash
                if (added / ((double) currentSize) >= threshold) {
                    currentSize *= 2;
                    ArrayList<LinkedList<Dimension>> newClusterTable = initializeTable(currentSize);

                    // Re-index dimensions
                    for (LinkedList<Dimension> cluster : clusterTable) {
                        for (Dimension dimension : cluster) {
                            int index = dimension.getNumber() % currentSize;
                            newClusterTable.get(index).addFirst(dimension);
                        }
                    }

                    clusterTable = newClusterTable;
                }

                StdIn.readLine();
            }

            // Connect clusters in table
            for (int i = 0; i < currentSize; i++) {
                LinkedList<Dimension> currentCluster = clusterTable.get(i);
                LinkedList<Dimension> prevCluster1 = clusterTable.get((i - 1 + currentSize) % currentSize);
                LinkedList<Dimension> prevCluster2 = clusterTable.get((i - 2 + currentSize) % currentSize);

                if (!prevCluster1.isEmpty()) {
                    currentCluster.addLast(prevCluster1.getFirst());
                }
                if (!prevCluster2.isEmpty()) {
                    currentCluster.addLast(prevCluster2.getFirst());
                }
            }
        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    public void printClusterTable(String filename) {
        try {
            StdOut.setFile(filename);

            for (int i = 0; i < clusterTable.size(); i++) {
                if (i > 0) {
                    StdOut.println();
                }

                LinkedList<Dimension> cluster = clusterTable.get(i);

                for (int j = 0; j < cluster.size(); j++) {
                    if (j > 0) {
                        StdOut.print(' ');
                    }

                    StdOut.print(cluster.get(j).getNumber());
                }
            }

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    private ArrayList<LinkedList<Dimension>> initializeTable(int size) {
        ArrayList<LinkedList<Dimension>> table = new ArrayList<>(size);

        for (int n = 0; n < size; n++) {
            table.add(new LinkedList<>());
        }

        return table;
    }

    public void loadPeople(String filename) {
        try {
            StdIn.setFile(filename);

            int people = StdIn.readInt();

            StdIn.readLine();

            for (int i = 0; i < people; i++) {
                int number = StdIn.readInt();
                String name = StdIn.readString();
                int signature = StdIn.readInt();

                Person person = new Person(number, name, signature);
                Dimension dimension = getDimension(number);

                dimension.addPerson(person);
            }

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    public void createGraph() {
        graph = new LinkedList<>();

        for (int i = 0; i < clusterTable.size(); i++) {
            LinkedList<Dimension> cluster = clusterTable.get(i);
            Dimension dimension = cluster.get(0);

            addVertext(dimension);

            for (int j = 1; j < cluster.size(); j++) {
                var edge = cluster.get(j);

                dimension.addEdge(edge.getNumber());
                addVertext(edge);
                edge.addEdge(dimension.getNumber());
            }
        }
    }

    public void printGraph(String filename) {
        try {
            StdOut.setFile(filename);

            for (Dimension vertex : graph) {
                StdOut.print(vertex.getNumber());

                for (int edge : vertex.getEdges()) {
                    StdOut.print(" ");
                    StdOut.print(edge);
                }

                StdOut.println();
            }

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    public void trackSpot(String filename) {
        try {
            StdIn.setFile(filename);

            int initialDimension = StdIn.readInt();
            int destinationDimension = StdIn.readInt();
            Set<Integer> visited = new HashSet<>();
            spotRoute = new ArrayList<>();

            trackSpotPath(initialDimension, destinationDimension, visited);

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    private boolean trackSpotPath(int currentDimension, int destinationDimension, Set<Integer> visited) {
        visited.add(currentDimension);
        spotRoute.add(currentDimension);

        if (currentDimension == destinationDimension) {
            return true;
        }

        for (int edge : getEdges(currentDimension)) {
            if (!visited.contains(edge)) {
                if (trackSpotPath(edge, destinationDimension, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void printSpotRoute(String filename) {
        try {
            StdOut.setFile(filename);

            for (int dimension : spotRoute) {
                StdOut.print(dimension);
                StdOut.print(' ');
            }

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    public void findAnomalyRoutes(String filename) {
        try {
            StdIn.setFile(filename);

            HashMap<Integer, Integer> lineage = new HashMap<>();
            Queue<Integer> queue = new LinkedList<>();
            anomalyRouteMap = new HashMap<>();
            hubDimensionNumber = StdIn.readInt();

            queue.add(hubDimensionNumber);
            lineage.put(hubDimensionNumber, -1); // Source has no parent

            while (!queue.isEmpty()) {
                int current = queue.poll();

                if (hasAnomalies(current)) {
                    ArrayList<Integer> route = buildPath(current, lineage);

                    if (hasSpiders(current)) {
                        anomalyRouteMap.put(current, route);
                    } else {
                        ArrayList<Integer> pathFromHub = new ArrayList<>(route);

                        pathFromHub.remove(0);
                        Collections.reverse(pathFromHub);

                        if (pathFromHub.addAll(route)) {
                            anomalyRouteMap.put(current, pathFromHub);
                        }
                    }
                }

                for (int dimension : getEdges(current)) {
                    if (lineage.get(dimension) == null) {
                        queue.add(dimension);
                        lineage.put(dimension, current);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    private ArrayList<Integer> buildPath(int vertex, HashMap<Integer, Integer> lineage) {
        ArrayList<Integer> path = new ArrayList<>();

        while (vertex != -1) {
            path.add(vertex);
            vertex = lineage.get(vertex);
        }

        return path;
    }

    public void collectPeople() {
        Dimension hub = getDimension(hubDimensionNumber);

        for (int dimensionNumber : anomalyRouteMap.keySet()) {
            if (dimensionNumber != hubDimensionNumber) {
                Dimension dimension = getDimension(dimensionNumber);
                ArrayList<Person> anomalies = dimension.getAnomalies();

                // Collect anomalies
                for (Person anomaly : anomalies) {
                    dimension.removePerson(anomaly);
                    hub.addPerson(anomaly);
                }

                // Collecting spider
                if (hasSpiders(dimensionNumber)) {
                    Person spider = dimension.getSpiders().get(0);

                    dimension.removePerson(spider);
                    hub.addPerson(spider);
                }
            }
        }
    }

    public void printAnomalyRoutesToHub(String filename) {
        try {
            StdOut.setFile(filename);

            for (int dimensionNumber : anomalyRouteMap.keySet()) {
                // Only print out routes for anomalies not in the hub
                if (dimensionNumber != hubDimensionNumber) {
                    Dimension dimension = getDimension(dimensionNumber);
                    ArrayList<Person> spiders = dimension.getSpiders();
                    ArrayList<Person> anomalies = dimension.getAnomalies();

                    for (Person anomaly : anomalies) {
                        StdOut.print(anomaly.getName() + " ");

                        if (spiders.size() > 0) {
                            StdOut.print(spiders.get(0).getName() + " ");
                        }

                        for (int path : anomalyRouteMap.get(dimensionNumber)) {
                            StdOut.print(path);
                            StdOut.print(' ');
                        }

                        StdOut.println();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void sendAnomaliesHome(String filename) {
        anomalyTimeMap = new HashMap<>();

        try {
            StdIn.setFile(filename);

            int anomaliesCount = StdIn.readInt();

            StdIn.readLine();

            // Load anomalies
            for (int i = 0; i < anomaliesCount; i++) {
                String name = StdIn.readString();
                int time = StdIn.readInt();

                anomalyTimeMap.put(name, time);
            }

            // Compute shortest routes
            Dimension hub = getDimension(hubDimensionNumber);
            data = findDistancesAndRoutes(hubDimensionNumber);

            for (String anomaly : anomalyTimeMap.keySet()) {
                Person person = hub.getPerson(anomaly);
                if (person != null) {
                    Dimension dimension = getDimension(person.getSignature());

                    hub.removePerson(person);
                    person.setDimension(person.getSignature());
                    dimension.addPerson(person);

                    if (data.getDistances().get(person.getSignature()) > anomalyTimeMap.get(anomaly)) {
                        dimension.setEvents(dimension.getEvents() - 1);
                        person.setSuccessfullySentHome(false);
                    } else {
                        person.setSuccessfullySentHome(true);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    public MachineData findDistancesAndRoutes(int sourceDimension) {
        HashMap<Integer, Integer> distances = new HashMap<>();
        HashMap<Integer, Integer> predecessors = new HashMap<>();
        Set<Integer> done = new HashSet<>();
        Queue<Dimension> fringe = new LinkedList<>();
        Dimension source = getDimension(sourceDimension);

        for (Dimension vertex : graph) {
            distances.put(vertex.getNumber(), Integer.MAX_VALUE);
            predecessors.put(vertex.getNumber(), null);
        }

        distances.put(source.getNumber(), 0);
        predecessors.put(source.getNumber(), null);

        fringe.add(source);

        while (!fringe.isEmpty()) {
            Dimension current = fringe.poll();

            done.add(current.getNumber());

            for (int connected : current.getEdges()) {
                if (!done.contains(connected)) {
                    Dimension dimension = getDimension(connected);
                    int distance = distances.get(current.getNumber()) + current.getWeight() + dimension.getWeight();

                    if (distances.get(connected) == Integer.MAX_VALUE) {
                        distances.put(connected, distance);
                        fringe.add(dimension);
                        predecessors.put(connected, current.getNumber());
                    } else if (distances.get(connected) > distance) {
                        distances.put(connected, distance);
                        predecessors.put(connected, current.getNumber());
                    }
                }
            }
        }

        var result = new MachineData();

        result.setDistances(distances);
        result.setLineages(predecessors);

        return result;
    }

    public void printReport(String filename) {
        try {
            StdOut.setFile(filename);

            for (String anomaly : anomalyTimeMap.keySet()) {
                Person person = getPerson(anomaly);
                Dimension dimension = getDimension(person.getSignature());

                StdOut.print(dimension.getEvents());
                StdOut.print(' ');
                StdOut.print(person.getName());
                StdOut.print(' ');
                StdOut.print((person.getSuccessfullySentHome() ? "SUCCESS" : "FAILED"));
                StdOut.print(' ');
                StdOut.print(routeToHome(person.getSignature()));
                StdOut.println();
            }

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    private String routeToHome(Integer dimension) {
        HashMap<Integer, Integer> routes = data.getLineages();
        String route = "";

        do {
            route = dimension + " " + route;
            dimension = routes.get(dimension);
        } while (dimension != null);

        return route;
    }

    public void saveMiles(String filename) {
        try {
            StdIn.setFile(filename);

            int spiderCount = StdIn.readInt();
            int threshold = StdIn.readInt();
            int time = StdIn.readInt();
            int meetDimensionNumber = StdIn.readInt();
            ArrayList<String> spiders = new ArrayList<>();

            StdIn.readLine();

            for (int i = 0; i < spiderCount; i++) {
                spiders.add(StdIn.readLine());
            }

            var meetupDimension = getDimension(meetDimensionNumber);
            var distances = findDistancesAndRoutes(meetDimensionNumber).getDistances();
            var canMakeIt = 0;

            for (String spider : spiders) {
                for (Dimension dimension : graph) {
                    Person person = dimension.getPerson(spider);

                    if (person != null) {
                        if (distances.get(dimension.getNumber()) <= time) {
                            canMakeIt++;
                        }

                        dimension.removePerson(person);
                        person.setDimension(meetDimensionNumber);
                        meetupDimension.addPerson(person);

                        break;
                    }
                }
            }

            milesSaved = canMakeIt >= threshold;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void printRescue(String filename) {
        try {
            StdOut.setFile(filename);
            StdOut.print(milesSaved ? "TRUE" : "FALSE");
        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    public void updateGraph(String filename) {
        try {
            StdIn.setFile(filename);

            int dimensionCount = StdIn.readInt();

            StdIn.readLine();

            for (int i = 0; i < dimensionCount; i++) {
                int number = StdIn.readInt();
                int events = StdIn.readInt();
                Dimension dimension = getDimension(number);

                if (dimension != null) {
                    dimension.setEvents(dimension.getEvents() - events);
                }
            }

            Set<Integer> toDelete = new HashSet<>();

            for (Dimension dimension : graph) {
                if (dimension.getEvents() <= 0) {
                    toDelete.add(dimension.getNumber());
                }
            }

            // Remove dimensions - both isolated and deleted
            for (int number : toDelete) {
                for (Dimension dimension : graph) {
                    dimension.removeEdge(number);
                }

                removeVertex(number);
            }

            toDelete.clear();

            for (Dimension dimension : graph) {
                if (dimension.getEdges().isEmpty()) {
                    toDelete.add(dimension.getNumber());
                }
            }

            for (int dimensionNumber : toDelete) {
                removeVertex(dimensionNumber);
            }

            // Deal with disconnected graph
            ArrayList<Set<Integer>> connectedComponents = findConnectedComponents();
            Set<Integer> largestComponent = connectedComponents.stream()
                    .max(Comparator.comparing(Set::size))
                    .orElse(Collections.emptySet());
            toDelete = getVertices();
            toDelete.removeAll(largestComponent);

            for (int number : toDelete) {
                removeVertex(number);
            }
        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }

    private ArrayList<Set<Integer>> findConnectedComponents() {
        ArrayList<Set<Integer>> components = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (Dimension dimension : graph) {
            if (!visited.contains(dimension.getNumber())) {
                Set<Integer> component = new HashSet<>();
                findConnectedComponentsHelper(dimension.getNumber(), visited, component);
                components.add(component);
            }
        }

        return components;
    }

    private void findConnectedComponentsHelper(int dimensionId,Set<Integer> visited, Set<Integer> component) {
        visited.add(dimensionId);
        component.add(dimensionId);

        for (Dimension dimension : graph) {
            if (!visited.contains(dimension.getNumber())) {
                findConnectedComponentsHelper(dimension.getNumber(), visited, component);
            }
        }
    }

    // Utilities
    private Dimension getDimension(int number) {
        for (Dimension dimension : graph) {
            if (dimension.getNumber() == number) {
                return dimension;
            }
        }

        return null;
    }

    private ArrayList<Integer> getEdges(int dimension) {
        return getDimension(dimension).getEdges();
    }

    private void addVertext(Dimension dimension) {
        for (Dimension vertex : graph) {
            if (vertex.getNumber() == dimension.getNumber()) {
                return;
            }
        }

        graph.addFirst(dimension);
    }

    private void removeVertex(int dimension) {
        int index = 0;

        for (Dimension vertex : graph) {
            if (vertex.getNumber() == dimension) {
                graph.remove(index);
                return;
            }

            index++;
        }
    }

    private Set<Integer> getVertices() {
        Set<Integer> vertices = new HashSet<>();

        for (Dimension vertex : graph) {
            vertices.add(vertex.getNumber());
        }

        return vertices;
    }

    private boolean hasAnomalies(int dimension) {
        return getDimension(dimension).getAnomalies().size() > 0;
    }

    private boolean hasSpiders(int dimension) {
        return getDimension(dimension).getSpiders().size() > 0;
    }

    private Person getPerson(String name) {
        for (Dimension vertex : graph) {
            Person person = vertex.getPerson(name);

            if (person != null) {
                return person;
            }
        }

        return null;
    }
}
