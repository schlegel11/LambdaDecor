# LambdaDecor

[![Build Status](https://travis-ci.org/schlegel11/LambdaDecor.svg?branch=master)](https://travis-ci.org/schlegel11/LambdaDecor)
[![Maven Central](https://img.shields.io/maven-central/v/de.schlegel11/lambda-decor.svg)](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22lambda-decor%22)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dfa7dfd1c9ce492cb447b7f8e9bcaa42)](https://www.codacy.com/app/marcel_4/LambdaDecor?utm_source=github.com&utm_medium=referral&utm_content=schlegel11/LambdaDecor&utm_campaign=badger)
[![Known Vulnerabilities](https://snyk.io/test/github/schlegel11/lambdadecor/badge.svg)](https://snyk.io/test/github/schlegel11/lambdadecor)

Decorator and strategy lambda mixture <br>
Use lambda's as a way to decorate your classes etc. with new functionality.
It is similar to the decorator or strategy pattern.

## Requirements

- JDK 8 and up

## Documentation

##### Version 1.2 (Latest)

For details see: [JavaDoc](https://schlegel11.github.io/LambdaDecor/releases/1.2/api/docs/)
 
## Usage

##### The Behaviour

A Behaviour is a class in which you can add different functionality. <br>
It wraps more or less a Function<T, T> which accepts and returns a specific type.

We create an Behaviour and add functionality like this:
```java
        Behaviour<String> stringBehaviour = DefaultBehaviour.newBehaviour();
        stringBehaviour = stringBehaviour.with(s -> s + " concat something"); // Add a Function<String, String> 
```

It is possible to chain more functionality by using the method "with(Function<T,T>)".
Further more the Behaviour class accespts also a Function<T, Unappliable>. The Unappliable is a functional interface
which can be performed after all Behaviour specific functionality is performed.

Lets add more functionality to the Behaviour, an Unappliable and execute the whole thing ;)

```java
        Behaviour<String> stringBehaviour = DefaultBehaviour.newBehaviour();
        stringBehaviour = stringBehaviour.with(s -> s + " concat something"); // Add a Function<String, String>

        stringBehaviour = stringBehaviour.with(s -> s.replace('h', '#'))
                                         .with(String::toLowerCase); /* Add more functionality ->
                                                                    the return value (in this case the string)
                                                                    is the argument for the next "with(Function<T,T>)" in the chain. */

        stringBehaviour = stringBehaviour.withUnapply(
                s -> () -> System.out.println("Something after")); // Add functionality with an Unappliable

        DecorPair<String> decorPair = stringBehaviour.apply("anything"); // Perform our Behaviour and returns an DecorPair

        System.out.println(decorPair._Behaviour); //Output behaviour value: "anyt#ing concat somet#ing"

        decorPair._Unapply.unapply(); //Perform the Unappliable of our Behaviour -> Outputs: "Something after"
```

You can chain as many "with(Function<T,T>)" or "withUnapply(Function<T, Unappliable>)" functionality as you want.

##### The LambdaDecor

The LambdaDecor class wraps in general an Behaviour and allows an easier handling.
For example the above Behaviour can be recreated with this LambdaDecor:

```java
        LambdaDecor<String> lambdaDecor = DefaultLambdaDecor.create(b -> b.with(s -> s + " concat something")
                                          .with(s -> s.replace('h', '#'))
                                          .with(String::toLowerCase)
                                          .withUnapply(
                                                  s -> () -> System.out.println("Something after")));

        System.out.println(lambdaDecor.apply("anything"));
        
        lambdaDecor.unapply();
```

### UI example

##### Construct the behaviour

A sample use case could be a decoration of a JavaFX button.
In this case we create a final class with static methods for a specific behaviour of a Button object.
```java
public final class MyButtonBehaviours {

    public static Button redButton(Button button) {
        button.setStyle("-fx-background-color: #ff3950"); // Change button color to red
        return button; // return our button
    }

    public static Unappliable helloOnClick(Button button) {
        button.setOnMouseClicked(
                event -> System.out.println("Hello")); // set a click listener and output "Hello" on click
        return () -> button.setOnMouseClicked(null); // On unapply set the listener to null
    }

    public static Unappliable onUnapplyGreenButton(Button button) {
        return () -> button.setStyle("-fx-background-color: #7fee59"); // Change button color to green
    }
}
```

##### Construct the lambda decor

Next we create our LambdaDecor with functionality of the ButtonBehaviour class.
```java
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        LambdaDecor<Button> buttonDecor = DefaultLambdaDecor.create(b -> b.with(MyButtonBehaviours::redButton)
                                                                          .withUnapply(
                                                                                  MyButtonBehaviours::helloOnClick)
                                                                          .withUnapply(
                                                                                  MyButtonBehaviours::onUnapplyGreenButton)); // Construct the decor object with its behaviour

        FlowPane root = new FlowPane(5, 5);
        root.getChildren()
            .add(buttonDecor.apply(new Button("MyButton"))); // Apply the behaviour to a button object
        root.getChildren()
            .add(buttonDecor.apply(new Button("MyButton"))); // Apply the behaviour to a button object


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        primaryStage.iconifiedProperty()
                    .addListener((c, o, n) -> buttonDecor.unapply()); // If the main window is iconified unapply the behaviour -> button color change to green and listener is set to null
    }


    public static void main(String[] args) {
        launch(args);
    }
}
```

### Summary

With the LambdaDecor its possible to add functionality to a specific type in a more functional oriented way.
Further more there is a loose coupling between our specific object and the functionality of it so we can exchange this easily.
