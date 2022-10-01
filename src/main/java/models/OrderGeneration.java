package models;

public class OrderGeneration {

    public static Order getDefault() {
        // "Space экзо-плантаго флюоресцентный традиционный-галактический бургер"
        String[] ingredients = {
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa73",
                "61c0c5a71d1f82001bdaaa74",
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa79"
        };
        return new Order(ingredients);
    }
}
