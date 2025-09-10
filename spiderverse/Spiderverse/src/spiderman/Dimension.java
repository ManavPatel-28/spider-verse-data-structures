package spiderman;

import java.util.ArrayList;

public class Dimension {
    private int number;
    private int events;
    private int weight;
    private ArrayList<Person> people;
    private ArrayList<Integer> edges;

    public Dimension(int number, int events, int weight) {
        this.number = number;
        this.events = events;
        this.weight = weight;
        this.people = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public void removePerson(Person person) {
        people.remove(person);
    }

    public void addEdge(int dimension) {
        edges.add(dimension);
    }

    public void removeEdge(int dimension) {
        ArrayList<Integer> newList = new ArrayList<>();

        for (int item : edges) {
            if(item!=dimension){
                newList.add(item);
            }
        }

        edges = newList;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int newNumber) {
        number = newNumber;
    }

    public int getEvents() {
        return events;
    }

    public void setEvents(int newCanonEvents) {
        events = newCanonEvents;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int newWeight) {
        weight = newWeight;
    }

    public ArrayList<Integer> getEdges() {
        return edges;
    }

    public ArrayList<Person> getAnomalies() {
        ArrayList<Person> anomalies = new ArrayList<>();

        for (Person person : people) {
            if (person.isAnomaly()) {
                anomalies.add(person);
            }
        }

        return anomalies;
    }

    public ArrayList<Person> getSpiders() {
        ArrayList<Person> spiders = new ArrayList<>();

        for (Person person : people) {
            if (person.isSpider()) {
                spiders.add(person);
            }
        }

        return spiders;
    }

    public Person getPerson(String name) {
        for (Person person : people) {
            if (person.getName().equalsIgnoreCase(name)) {
                return person;
            }
        }

        return null;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }
}
