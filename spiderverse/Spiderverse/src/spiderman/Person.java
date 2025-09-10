package spiderman;

public class Person {
    private String name;
    private int signature;
    private int dimension;
    private boolean successfullySentHome;

    public Person(int dimension, String name, int signature) {
        this.dimension = dimension;
        this.name = name;
        this.signature = signature;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int newDimension) {
        dimension = newDimension;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public int getSignature() {
        return signature;
    }

    public boolean isAnomaly(){
        return !isSpider();
    }

    public boolean isSpider(){
        return dimension == signature;
    }

    public boolean getSuccessfullySentHome() {
        return successfullySentHome;
    }

    public void setSuccessfullySentHome(boolean status){
        successfullySentHome = status;
    }
}
